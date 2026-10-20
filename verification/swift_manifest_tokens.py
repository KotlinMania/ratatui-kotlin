"""Shared structural manifest tokenizer; comments and strings are not edit anchors."""
import re

def require(ok, message):
    if not ok:
        raise ValueError(message)

def tokens(text):
    """Swift manifest structural tokens with offsets; comments/strings never become edit anchors."""
    result = []
    i = 0
    while i < len(text):
        if text[i].isspace():
            i += 1
        elif text.startswith('//', i):
            end = text.find('\n', i)
            i = len(text) if end < 0 else end
        elif text.startswith('/*', i):
            depth = 1
            i += 2
            while depth and i < len(text):
                if text.startswith('/*', i):
                    depth += 1
                    i += 2
                elif text.startswith('*/', i):
                    depth -= 1
                    i += 2
                else:
                    i += 1
            require(depth == 0, 'Unterminated Swift comment')
        elif text.startswith('"""', i):
            end = text.find('"""', i + 3)
            require(end >= 0, 'Unterminated Swift raw string')
            i = end + 3
        elif text[i] in '\"\'':
            quote = text[i]
            i += 1
            while i < len(text) and text[i] != quote:
                i += 2 if text[i] == '\\' else 1
            require(i < len(text), 'Unterminated Swift string')
            i += 1
        else:
            match = re.match(r'[A-Za-z_][A-Za-z_0-9]*|.', text[i:])
            result.append((match[0], i, i + len(match[0])))
            i += len(match[0])
    return result
