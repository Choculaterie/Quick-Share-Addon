# Minecraft Fabric Mod Version Migration Guide

## Overview
This guide explains how to change a Fabric mod to target any Minecraft version and find the correct compatible versions for all dependencies. Use this guide whether you're upgrading, downgrading, or switching to any specific Minecraft version.

## Target Version
Replace `X.XX.X` throughout this guide with your desired Minecraft version (e.g., 1.20.1, 1.21.0, 1.21.10, etc.)

## Step-by-Step Process to Find Compatible Versions

### 1. Finding Fabric Loader Version

**Where to Search:** 
- https://fabricmc.net/develop
- https://github.com/FabricMC/fabric-example-mod (check different branches for version examples)

**Steps:**
1. **Option A - Fabric MC Website:**
   - Go to Fabric MC website
   - Click on "Versions" or "Develop" section
   - Look for your **target Minecraft version** (e.g., 1.20.1, 1.21.0, 1.21.10)
   - Find the recommended Fabric Loader version for that MC version

2. **Option B - Fabric Example Mod GitHub:**
   - Go to https://github.com/FabricMC/fabric-example-mod
   - Look at branches for your target MC version (e.g., `1.20`, `1.21`)
   - Open `gradle.properties` file in that branch
   - See the versions used in the official example

3. Note: Fabric Loader versions are usually backward compatible, but use the recommended one for best results

**Result Format:** `loader_version=X.XX.X` (e.g., `0.15.11`, `0.16.5`, `0.18.2`)

**Example:** For MC 1.21.10, you might use `loader_version=0.18.2`

### 2. Finding Fabric API Version

**Where to Search:** 
- https://modrinth.com/mod/fabric-api/versions
- https://www.curseforge.com/minecraft/mc-mods/fabric-api/files
- https://github.com/FabricMC/fabric-example-mod (check branches for reference versions)

**Steps:**
1. **Option A - Modrinth/CurseForge:**
   - Go to Fabric API on Modrinth or CurseForge
   - Click "Versions" tab
   - Use the filters:
     - Game Version: Select **your target MC version** (e.g., "1.20.1", "1.21.10")
     - Mod Loader: Select "Fabric"
   - Look for the latest stable version for your target MC version
   - The version format is usually: `X.Y.Z+MC_VERSION` (e.g., `0.92.2+1.20.1` or `0.138.4+1.21.10`)

2. **Option B - Fabric Example Mod GitHub:**
   - Go to https://github.com/FabricMC/fabric-example-mod
   - Switch to the branch for your target MC version
   - Check `gradle.properties` for the `fabric_version` used

**Key Points:**
- Make sure the version number ends with `+YOUR_MC_VERSION`
- Choose releases over beta/alpha versions when possible
- Check the changelog for any breaking changes

**Result Format:** `fabric_version=X.Y.Z+MC_VERSION`

**Examples:** 
- For MC 1.20.1: `fabric_version=0.92.2+1.20.1`
- For MC 1.21.10: `fabric_version=0.138.4+1.21.10`

### 3. Finding Litematica Version

**Where to Search:** https://modrinth.com/mod/litematica/versions

**Steps:**
1. Go to Litematica on Modrinth: https://modrinth.com/mod/litematica
2. Click "Versions" tab
3. Filter by game version **your target MC version**
4. Find the latest compatible version
5. **CRITICAL:** Click on the version and check the "Dependencies" section to see required MaLiLib version

**Important Notes:**
- Litematica requires MaLiLib (always listed in dependencies)
- Version numbers don't always match between Litematica and MaLiLib
- Read the version description for compatibility notes
- Some MC versions may have multiple Litematica versions - choose the latest stable

**Result Format:** `litematica_version=X.XX.X`

**Examples:**
- For MC 1.20.1: `litematica_version=0.17.3`
- For MC 1.21.10: `litematica_version=0.24.5`

### 4. Finding MaLiLib Version

**Where to Search:** https://modrinth.com/mod/malilib/versions

**Steps:**
1. **IMPORTANT:** First check what Litematica requires (from Step 3)
   - Go back to the Litematica version page on Modrinth
   - Click on your chosen Litematica version
   - Look at the "Dependencies" section - it will show the required MaLiLib version

2. Go to MaLiLib on Modrinth: https://modrinth.com/mod/malilib
3. Click "Versions" tab
4. Filter by game version **your target MC version**
5. Find the version that matches or is newer than what Litematica requires

**Critical Step:**
- Always verify the MaLiLib version matches what Litematica expects
- The MaLiLib version shown in Litematica's dependencies is the **minimum required** version
- You can use that exact version or a newer compatible one for the same MC version

**Result Format:** `malilib_version=X.XX.X`

**Examples:**
- For MC 1.20.1 (with Litematica 0.17.3): `malilib_version=0.18.1`
- For MC 1.21.10 (with Litematica 0.24.5): `malilib_version=0.26.6`

### 5. Finding Loom Version

**Where to Search:** https://github.com/FabricMC/fabric-loom/releases

**Steps:**
1. Go to Fabric Loom GitHub releases
2. Check recent stable releases
3. Look for versions that support your Minecraft version
4. Generally, recent versions (1.6+) support multiple MC versions
5. For older MC versions (1.18 and below), you may need older Loom versions

**Result Format:** `loom_version=X.XX` or `loom_version=X.XX-SNAPSHOT`

**Examples:**
- For MC 1.20.x: `loom_version=1.6-SNAPSHOT` or higher
- For MC 1.21.x: `loom_version=1.14-SNAPSHOT` or higher
- Check Loom release notes for compatibility

## Files to Modify

### gradle.properties
Replace the values with your target versions found in steps 1-5:

```properties
minecraft_version=X.XX.X        # Your target MC version
loader_version=X.XX.X           # From Step 1
loom_version=X.XX-SNAPSHOT      # From Step 5
fabric_version=X.Y.Z+X.XX.X     # From Step 2 (must match MC version)
litematica_version=X.XX.X       # From Step 3
malilib_version=X.XX.X          # From Step 4
```

**Example for MC 1.21.10:**
```properties
minecraft_version=1.21.10
loader_version=0.18.2
loom_version=1.14-SNAPSHOT
fabric_version=0.138.4+1.21.10
litematica_version=0.24.5
malilib_version=0.26.6
```

**Example for MC 1.20.1:**
```properties
minecraft_version=1.20.1
loader_version=0.15.11
loom_version=1.6-SNAPSHOT
fabric_version=0.92.2+1.20.1
litematica_version=0.17.3
malilib_version=0.18.1
```

### fabric.mod.json (depends section)
Update the minecraft version constraint to match your target:

```json
"depends": {
    "fabricloader": ">=X.XX.X",              # Minimum loader version
    "minecraft": ">=X.XX.X <X.XX.X+1",       # Your MC version range
    "java": ">=21",                          # Java 21 for MC 1.20.5+, Java 17 for older
    "fabric-api": "*",
    "litematica": "*"
}
```

**Example for MC 1.21.10:**
```json
"depends": {
    "fabricloader": ">=0.18.2",
    "minecraft": ">=1.21.10 <1.21.11",
    "java": ">=21",
    "fabric-api": "*",
    "litematica": "*"
}
```

**Example for MC 1.20.1:**
```json
"depends": {
    "fabricloader": ">=0.15.11",
    "minecraft": ">=1.20.1 <1.20.2",
    "java": ">=17",
    "fabric-api": "*",
    "litematica": "*"
}
```

## Verification Steps

After making changes:

1. **Clean the build:**
   ```bash
   ./gradlew clean
   ```

2. **Refresh dependencies:**
   ```bash
   ./gradlew --refresh-dependencies
   ```

3. **Build the project:**
   ```bash
   ./gradlew build
   ```

4. **Check for errors:**
   - Look at compilation errors
   - Check if dependencies resolve correctly
   - Verify mod loads in development environment

5. **Run the client:**
   ```bash
   ./gradlew runClient
   ```

## Important Java Version Requirements

Different Minecraft versions require different Java versions:

- **MC 1.20.5 and newer:** Requires Java 21+
- **MC 1.18 - 1.20.4:** Requires Java 17+
- **MC 1.17:** Requires Java 16+
- **MC 1.16 and older:** Requires Java 8+

Make sure your `fabric.mod.json` specifies the correct Java version for your target MC version.

## Common Issues and Solutions

### Issue: "Could not find" dependency errors
**Solution:** 
- Check if the version exists on the repository
- Verify repository URLs in build.gradle are correct
- Try slightly different version numbers

### Issue: API incompatibilities
**Solution:**
- Check if there were breaking changes between versions
- Update code to use compatible API methods
- Check mod documentation for migration guides

### Issue: Mixin failures
**Solution:**
- Verify target methods/classes still exist in target version
- Check Minecraft mappings changes
- Update mixin targets if needed

## Quick Reference: Version Finding Checklist

- [ ] Check Fabric MC website for loader version
- [ ] Find Fabric API on Modrinth/CurseForge filtered by MC version
- [ ] Find Litematica on Modrinth filtered by MC version
- [ ] Check Litematica's dependencies for required MaLiLib version
- [ ] Find matching MaLiLib version
- [ ] Verify Loom version is recent enough
- [ ] Update gradle.properties
- [ ] Update fabric.mod.json minecraft version constraint
- [ ] Clean and rebuild project
- [ ] Test in development environment

## Tips for Claude Sonnet (or any AI assistant)

When helping users find versions:

1. **Use the right sources:**
   - For Fabric versions (Loader, API, Loom): Check https://github.com/FabricMC/fabric-example-mod branches
   - For Litematica: Use https://modrinth.com/mod/litematica/versions
   - For MaLiLib: Use https://modrinth.com/mod/malilib/versions (but always verify with Litematica's dependencies)

2. **Always filter by exact Minecraft version** - Don't assume versions are compatible across minor releases

3. **Check dependencies first** - Litematica's dependency list on Modrinth shows the exact MaLiLib version required

4. **Version format matters** - Fabric API uses `+MC_VERSION` suffix (e.g., `0.138.4+1.21.10`)

5. **Use official sources** - Modrinth is the primary source for Litematica and MaLiLib; GitHub fabric-example-mod for Fabric versions

6. **Cross-reference everything** - Always verify dependency versions match what parent mods require

7. **Prefer stable releases** - Choose release versions over snapshots/beta/alpha unless specifically needed

## Resources

### Primary Sources for Version Finding:
- **Fabric Example Mod (GitHub):** https://github.com/FabricMC/fabric-example-mod - Best for finding compatible Fabric Loader, Loom, and Fabric API versions
- **Litematica (Modrinth):** https://modrinth.com/mod/litematica - For Litematica versions and dependency requirements
- **MaLiLib (Modrinth):** https://modrinth.com/mod/malilib - For MaLiLib versions (always check Litematica dependencies first)

### Additional Resources:
- **Fabric MC:** https://fabricmc.net/
- **Modrinth:** https://modrinth.com/
- **CurseForge:** https://www.curseforge.com/
- **Fabric Loom:** https://github.com/FabricMC/fabric-loom
- **Fabric API:** https://modrinth.com/mod/fabric-api

## Version History for This Project

Track your version changes here:

| Date | MC Version | Fabric Loader | Fabric API | Litematica | MaLiLib | Notes |
|------|------------|---------------|------------|------------|---------|-------|
| 2025-12-18 | 1.21.10 | 0.18.2 | 0.138.4+1.21.10 | 0.24.5 | 0.26.6 | Initial version |

---

*Last Updated: December 18, 2025*

