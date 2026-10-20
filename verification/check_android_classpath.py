"""Reject duplicate bytecode in resolved Android AAR/JAR artifacts, including AAR libs/."""
import argparse
import io
import json
import zipfile
from pathlib import Path


def duplicates(paths):
    owners = {}
    for path in sorted({Path(p).resolve() for p in paths}):
        with zipfile.ZipFile(path) as artifact:
            if path.suffix == '.aar':
                jars = [(f'{path}!/{n}', artifact.read(n)) for n in artifact.namelist()
                        if n == 'classes.jar' or (n.startswith('libs/') and n.endswith('.jar'))]
            elif path.suffix == '.jar':
                jars = [(str(path), path.read_bytes())]
            else:
                raise ValueError(f'Unsupported Android classpath artifact: {path}')
        for owner, data in jars:
            with zipfile.ZipFile(io.BytesIO(data)) as jar:
                for name in jar.namelist():
                    if name.endswith('.class') and not name.endswith('module-info.class'):
                        owners.setdefault(name, []).append(owner)
    return {name: sources for name, sources in sorted(owners.items()) if len(sources) > 1}


def inspect_configurations(configurations):
    if not configurations or any(not paths for paths in configurations.values()):
        raise ValueError('Each resolved configuration must contain classpath artifacts')
    return {scope: duplicates(paths) for scope, paths in configurations.items()}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('classpath', type=Path, help='One resolved raw AAR/JAR path per line')
    parser.add_argument('--receipt', required=True, type=Path)
    parser.add_argument('--configurations', action='store_true',
                        help='Read JSON mapping configuration names to resolved artifact paths; never union configurations')
    args = parser.parse_args()
    if args.configurations:
        configurations = json.loads(args.classpath.read_text())
    else:
        configurations = {'classpath': [line for line in args.classpath.read_text().splitlines() if line.strip()]}
    found = inspect_configurations(configurations)
    receipt = {'status': 'failed' if any(found.values()) else 'passed',
               'artifacts_by_configuration': configurations, 'duplicates_by_configuration': found}
    args.receipt.parent.mkdir(parents=True, exist_ok=True)
    args.receipt.write_text(json.dumps(receipt, indent=2) + '\n')
    print(json.dumps(receipt, indent=2))
    return any(found.values())


if __name__ == '__main__':
    raise SystemExit(main())
