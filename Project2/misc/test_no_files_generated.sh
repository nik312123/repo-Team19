#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

files_generated="$(git ls-files --exclude-standard --others "${DIR}/../testing/test-resources/")" && test -z "$files_generated"
