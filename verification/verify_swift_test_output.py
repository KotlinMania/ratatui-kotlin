"""Reject a successful Swift command that executed zero Swift Testing tests."""
import argparse
import json
import re
from pathlib import Path


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("log", type=Path)
    parser.add_argument("--receipt", required=True, type=Path)
    args = parser.parse_args()
    result = re.search(r"Test run with ([1-9]\d*) tests?[^\n]*passed", args.log.read_text())
    if result is None:
        raise SystemExit("No successful nonzero Swift Testing execution found")
    receipt = {"status": "passed", "executed_tests": int(result[1]), "log": str(args.log.resolve())}
    args.receipt.parent.mkdir(parents=True, exist_ok=True)
    args.receipt.write_text(json.dumps(receipt, indent=2) + "\n")


if __name__ == "__main__":
    main()
