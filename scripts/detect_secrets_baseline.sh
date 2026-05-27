#!/bin/bash
# Single source of truth for detect-secrets arguments.
#
# Usage:
#   scripts/detect_secrets_baseline.sh scan   # Regenerate .secrets.baseline
#   scripts/detect_secrets_baseline.sh audit  # Interactively audit .secrets.baseline
#   scripts/detect_secrets_baseline.sh        # Check for new secrets vs baseline
#
set -e

DETECT_SECRETS_ARGS=(
    --exclude-files '\.secrets..*'
    --exclude-files '\.git.*'
    --exclude-files '\.pre-commit-config\.yaml'
    --exclude-files '\.venv'
    --exclude-files 'venv'
    --exclude-files 'dist'
    --exclude-files 'build'
)

compare_secrets() {
    diff \
        <(python3 -c "
import json, sys
with open(sys.argv[1]) as f: data = json.load(f)
lines = [f\"{k},{s['hashed_secret']}\" for k, v in data.get('results', {}).items() for s in v]
print('\n'.join(sorted(lines)))
" "$1") \
        <(python3 -c "
import json, sys
with open(sys.argv[1]) as f: data = json.load(f)
lines = [f\"{k},{s['hashed_secret']}\" for k, v in data.get('results', {}).items() for s in v]
print('\n'.join(sorted(lines)))
" "$2") \
        >/dev/null
}

if [ "$1" = "scan" ]; then
    detect-secrets scan "${DETECT_SECRETS_ARGS[@]}" > .secrets.baseline
    echo "Updated .secrets.baseline"
    echo "Next step: run 'scripts/detect_secrets_baseline.sh audit' to review and classify detected secrets."
elif [ "$1" = "audit" ]; then
    detect-secrets audit .secrets.baseline
else
    # Check 1: Fail if any secrets in the baseline have not been audited
    unaudited=$(python3 -c "
import json, sys
with open('.secrets.baseline') as f: data = json.load(f)
count = sum(1 for v in data.get('results', {}).values() for s in v if 'is_secret' not in s)
print(count)
")
    if [ "$unaudited" -gt 0 ]; then
        echo "⚠️ Attention Required! ⚠️" >&2
        echo "$unaudited secret(s) in .secrets.baseline have not been audited." >&2
        echo "Run 'scripts/detect_secrets_baseline.sh audit' to review and classify each detected secret." >&2
        exit 1
    fi

    # Check 2: Fail if any new secrets are detected that are not in the baseline
    cp .secrets.baseline .secrets.new
    detect-secrets scan "${DETECT_SECRETS_ARGS[@]}" --baseline .secrets.new

    if ! compare_secrets .secrets.baseline .secrets.new; then
        echo "⚠️ Attention Required! ⚠️" >&2
        echo "New secrets have been detected in your recent commit. Due to security concerns, we cannot display detailed information here and we cannot proceed until this issue is resolved." >&2
        echo "" >&2
        echo "Please follow the steps below on your local machine to reveal and handle the secrets:" >&2
        echo "" >&2
        echo "1️⃣ Run the 'detect-secrets' tool on your local machine. This tool will identify and clean up the secrets. You can find detailed instructions at this link: https://nasa-ammos.github.io/slim/continuous-testing/starter-kits/#detect-secrets" >&2
        echo "" >&2
        echo "2️⃣ After cleaning up the secrets, commit your changes and re-push your update to the repository." >&2
        echo "" >&2
        echo "Your efforts to maintain the security of our codebase are greatly appreciated!" >&2
        rm -f .secrets.new
        exit 1
    fi

    rm -f .secrets.new
fi
