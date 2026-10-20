"""Apply explicit Swift 6 package mode without rewriting generated Swift APIs."""
import argparse
import hashlib
import json
import re
from pathlib import Path

from swift_manifest_tokens import tokens, require


def enforce(text):
    header = re.match(r'// swift-tools-version:\s*(\d+)\.(\d+)(?:\.\d+)?[^\n]*(?:\n|$)', text)
    require(header is not None, 'Missing Swift tools version header')
    require(int(header[1]) <= 6, 'Newer Swift tools manifest requires review')
    ts = tokens(text)
    starts = [i for i in range(len(ts) - 1) if [t[0] for t in ts[i:i + 2]] == ['Package', '(']]
    require(len(starts) == 1, 'Unsupported/ambiguous Package initializer')
    opening = starts[0] + 1
    stack = ['(']
    args = {}
    at_argument = True
    closing = None
    for i in range(opening + 1, len(ts)):
        value = ts[i][0]
        if len(stack) == 1:
            if value == ')':
                closing = i
                break
            if value == ',':
                at_argument = True
                continue
            if at_argument:
                require(i + 1 < len(ts) and ts[i + 1][0] == ':', 'Unsupported package argument')
                require(value not in args, 'Duplicate package argument')
                args[value] = i
                at_argument = False
        if value in ['(', '[', '{']:
            stack.append(value)
        elif value in [')', ']', '}']:
            require(stack[-1] == {')': '(', ']': '[', '}': '{'}[value], 'Unbalanced Swift manifest')
            stack.pop()
    require(closing is not None, 'Unclosed Package initializer')
    require('targets' in args, 'Generated package must declare its targets')
    require('swiftLanguageVersions' not in args, 'Legacy explicit Swift language override requires owner review')
    # Per-target modes can override a package mode. Never silently preserve a weaker override.
    for i, token in enumerate(ts):
        if token[0] == 'swiftLanguageMode':
            require([t[0] for t in ts[i:i + 5]] == ['swiftLanguageMode', '(', '.', 'v6', ')'],
                    'Target-specific Swift language mode must be v6')
    edits = [] if int(header[1]) >= 6 else [(0, header.end(), '// swift-tools-version: 6.0\n')]
    if 'platforms' not in args:
        position = ts[min(args[k] for k in ['products', 'dependencies', 'targets'] if k in args)][1]
        indent = text[text.rfind('\n', 0, position) + 1:position]
        separator = '\n' + indent if not indent.strip() else ' '
        edits.append((position, position, 'platforms: [.macOS("15.0")],' + separator))
    if 'swiftLanguageModes' in args:
        i = args['swiftLanguageModes']
        require([t[0] for t in ts[i:i + 6]] == ['swiftLanguageModes', ':', '[', '.', 'v6', ']'],
                'Explicit package language modes must be v6 only')
        require(ts[i + 6][0] in [',', ')'], 'Multiple package language modes require review')
    else:
        c_arguments = [args[k] for k in ['cLanguageStandard', 'cxxLanguageStandard'] if k in args]
        if c_arguments:
            pos = ts[min(c_arguments)][1]
            indent = text[text.rfind('\n', 0, pos) + 1:pos]
            require(not indent.strip(), 'Inline C standard arguments require review')
            edits.append((pos, pos, 'swiftLanguageModes: [.v6],\n' + indent))
        else:
            last = ts[closing - 1]
            if last[0] != ',':
                edits.append((last[2], last[2], ','))
            edits.append((ts[closing][1], ts[closing][1], '\n    swiftLanguageModes: [.v6]\n'))
    for start, end, value in sorted(edits, reverse=True):
        text = text[:start] + value + text[end:]
    return text


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('manifest', type=Path)
    parser.add_argument('--receipt', type=Path, required=True)
    args = parser.parse_args()
    before = args.manifest.read_text()
    after = enforce(before)
    if after != before:
        args.manifest.write_text(after)
    receipt = {'manifest': str(args.manifest.resolve()), 'swift_language_mode': '6',
               'scope': 'Every Swift target in this generated package, including dependency modules',
               'sha256': hashlib.sha256(after.encode()).hexdigest(), 'swift_api_sources_modified': False}
    args.receipt.parent.mkdir(parents=True, exist_ok=True)
    args.receipt.write_text(json.dumps(receipt, indent=2) + '\n')


if __name__ == '__main__':
    main()
