#!/usr/bin/env bash
#
# rename.sh — one-command project rename.
#
# Usage:
#   ./rename.sh com.myteam.myapp "My App"
#
# What it does:
#   1. Renames the Kotlin package from com.vdharmani.starter → <new-package>
#      across all .kt source files (replaces the package declaration AND
#      every import that references it).
#   2. Moves the source directories so they match the new package path.
#   3. Updates `namespace` in every module's build.gradle.kts.
#   4. Updates `applicationId` in app/build.gradle.kts.
#   5. Updates `rootProject.name` in settings.gradle.kts.
#   6. Updates the app label in res/values/strings.xml.
#   7. Removes the existing .git directory so you start with fresh history.
#
# It does NOT:
#   - Update gradle wrapper, dependencies, JDK version, etc.
#   - Change the JitPack-published libraries (imagepicker/subscription) since
#     those are vdharmani/* on purpose.
#
# Requires: bash, find, sed, mv. Works on macOS and Linux.

set -euo pipefail

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 com.myteam.myapp [\"My App\"]"
    echo ""
    echo "  com.myteam.myapp  — the new applicationId / package name (lowercase, dot-separated)"
    echo "  \"My App\"          — optional display name (defaults to last segment, capitalized)"
    exit 1
fi

OLD_PACKAGE="com.vdharmani.starter"
OLD_PATH="com/vdharmani/starter"
NEW_PACKAGE="$1"
NEW_PATH="$(echo "$NEW_PACKAGE" | tr '.' '/')"
NEW_LABEL="${2:-$(echo "${NEW_PACKAGE##*.}" | tr '[:lower:]' '[:upper:]' | head -c 1)$(echo "${NEW_PACKAGE##*.}" | cut -c 2-)}"

if [[ "$NEW_PACKAGE" == "$OLD_PACKAGE" ]]; then
    echo "✗ New package is the same as the old one. Nothing to do."
    exit 1
fi

if ! [[ "$NEW_PACKAGE" =~ ^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)+$ ]]; then
    echo "✗ Package name '$NEW_PACKAGE' looks invalid."
    echo "  Expected: lowercase segments separated by dots, e.g. com.myteam.myapp"
    exit 1
fi

echo "→ Renaming $OLD_PACKAGE → $NEW_PACKAGE"
echo "→ App label: $NEW_LABEL"
echo ""

# 1. Find all .kt files that mention the old package and rewrite them.
echo "[1/6] Rewriting package + import references in .kt files…"
find . \
    -path ./.gradle -prune -o \
    -path ./.idea -prune -o \
    -path "*/build" -prune -o \
    -name "*.kt" -print | while read -r file; do
    if grep -q "$OLD_PACKAGE" "$file"; then
        if [[ "$(uname)" == "Darwin" ]]; then
            sed -i '' "s|$OLD_PACKAGE|$NEW_PACKAGE|g" "$file"
        else
            sed -i "s|$OLD_PACKAGE|$NEW_PACKAGE|g" "$file"
        fi
    fi
done

# 2. Move source directories so they match the new package path.
echo "[2/6] Moving source directories…"
find . -type d -path "*/java/$OLD_PATH" | while read -r dir; do
    parent="$(dirname "$dir")"
    new_dir="$parent/$NEW_PATH"
    if [[ "$dir" != "$new_dir" ]]; then
        mkdir -p "$(dirname "$new_dir")"
        # Move contents only — the parent might overlap if packages share a prefix.
        if [[ -d "$new_dir" ]]; then
            # Destination already exists; move children instead.
            mv "$dir"/* "$new_dir"/ 2>/dev/null || true
            rmdir "$dir" 2>/dev/null || true
        else
            mv "$dir" "$new_dir"
        fi
    fi
done

# Clean up empty old package directories.
find . -type d -path "*/java/com/vdharmani" -empty -delete 2>/dev/null || true
find . -type d -path "*/java/com" -empty -delete 2>/dev/null || true

# 3. Update `namespace` in every build.gradle.kts.
echo "[3/6] Updating namespace in build.gradle.kts files…"
find . -name "build.gradle.kts" -not -path "*/build/*" | while read -r f; do
    if grep -q "namespace = \"$OLD_PACKAGE" "$f"; then
        if [[ "$(uname)" == "Darwin" ]]; then
            sed -i '' "s|namespace = \"$OLD_PACKAGE|namespace = \"$NEW_PACKAGE|g" "$f"
        else
            sed -i "s|namespace = \"$OLD_PACKAGE|namespace = \"$NEW_PACKAGE|g" "$f"
        fi
    fi
done

# 4. Update applicationId in app/build.gradle.kts.
echo "[4/6] Updating applicationId in app/build.gradle.kts…"
if [[ "$(uname)" == "Darwin" ]]; then
    sed -i '' "s|applicationId = \"$OLD_PACKAGE\"|applicationId = \"$NEW_PACKAGE\"|g" app/build.gradle.kts
else
    sed -i "s|applicationId = \"$OLD_PACKAGE\"|applicationId = \"$NEW_PACKAGE\"|g" app/build.gradle.kts
fi

# 5. Update rootProject.name in settings.gradle.kts.
echo "[5/6] Updating rootProject.name in settings.gradle.kts…"
new_root="$(echo "${NEW_PACKAGE##*.}" | tr '[:upper:]' '[:lower:]')"
if [[ "$(uname)" == "Darwin" ]]; then
    sed -i '' "s|rootProject.name = \"sampleproject-android\"|rootProject.name = \"$new_root\"|g" settings.gradle.kts
else
    sed -i "s|rootProject.name = \"sampleproject-android\"|rootProject.name = \"$new_root\"|g" settings.gradle.kts
fi

# 6. Update the app label.
echo "[6/6] Updating app label in app/src/main/res/values/strings.xml…"
if [[ "$(uname)" == "Darwin" ]]; then
    sed -i '' "s|<string name=\"app_name\">Starter</string>|<string name=\"app_name\">$NEW_LABEL</string>|g" app/src/main/res/values/strings.xml
else
    sed -i "s|<string name=\"app_name\">Starter</string>|<string name=\"app_name\">$NEW_LABEL</string>|g" app/src/main/res/values/strings.xml
fi

echo ""
echo "✓ Rename complete."
echo ""
echo "Next steps:"
echo "  1. Sanity-check the rename:   ./gradlew assembleDebug"
echo "  2. Update BASE_URL:           core/network/build.gradle.kts → buildConfigField(\"String\", \"BASE_URL\", …)"
echo "  3. Update REVENUECAT_KEY:     feature/premium/build.gradle.kts (if using subscriptions)"
echo "  4. Drop the starter git history and start fresh:"
echo "       rm -rf .git && git init && git add -A && git commit -m \"Initial commit\""
echo ""
