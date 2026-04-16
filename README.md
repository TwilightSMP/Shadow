# ✦︱Shadow
> The ultimate cinematic SMP nickname plugin for Twilight SMP — and any server that wants it.

![License](https://img.shields.io/badge/license-MIT-purple?style=flat-square)
![Minecraft](https://img.shields.io/badge/minecraft-1.21.x-darkgreen?style=flat-square)
![API](https://img.shields.io/badge/api-Paper-blue?style=flat-square)
![Build](https://img.shields.io/badge/build-Maven-orange?style=flat-square)
![Status](https://img.shields.io/badge/status-active-brightgreen?style=flat-square)

Shadow is a free, open-source, fully persistent Minecraft SMP plugin that lets players adopt custom nicknames and random skins with full cinematic flair. Built for storytelling servers, SMPs, and roleplay communities.

---

## ✦ Features

- `/nick <name>` — Set a custom nickname with `&`-based Minecraft color codes
- `/nick random` — Get a random nickname from the curated SMP pool
- `/realnick [player]` — Reveal the true username behind any nickname
- `/skin random` — Adopt a random cinematic skin from `skins.json`
- **SMP Role Flair** — Automatic prefix injection based on permissions:
  - `shadow.role.relic` → `✦︱Relic <name>`
  - `shadow.role.cursed` → `✦︱Cursed <name>`
  - `shadow.role.dead` → `✦︱Dead <name>`
- **Server-wide cinematic broadcasts** for all nick and skin changes
- **Full JSON persistence** across restarts — no database required
- **Auto-detects the latest Minecraft release** from Mojang's API on startup
- Fully modular and maintainable — one class, one responsibility

---

## ✦ Requirements

| Requirement | Version |
|---|---|
| Java | 21+ |
| Server Software | [PaperMC](https://papermc.io) 1.21.x |
| Build Tool | Maven 3.8+ |

> Shadow is built against Paper API. It will not compile against vanilla CraftBukkit or Spigot due to `PlayerProfile`/`PlayerTextures` usage for skin mutation.

---

## ✦ Installation

### Option A — Pre-built JAR
1. Download the latest JAR from [Releases](../../releases).
2. Drop it into your server's `plugins/` folder.
3. Restart the server.

### Option B — Build from Source
```bash
git clone https://github.com/your-org/Shadow.git
cd Shadow
mvn clean package
```
The shaded JAR will be at `target/Shadow-1.0.jar`. Drop it into `plugins/`.

---

## ✦ Configuration

No `config.yml` is required. Shadow auto-generates its data directory on first run:

```
plugins/Shadow/
└── data/
    ├── nicknames.json   # UUID → active nickname
    ├── originals.json   # UUID → original Mojang username
    └── skins.json       # Skin pool for /skin random
```

### Adding Custom Skins to `skins.json`

```json
{
  "random_skins": [
    {
      "name": "Your Skin Name",
      "url": "https://textures.minecraft.net/texture/<hash>"
    }
  ]
}
```

To get a valid texture hash, fetch it from Mojang's session server:
```
GET https://sessionserver.mojang.com/session/minecraft/profile/<uuid>?unsigned=false
```
Extract the `value` field, base64-decode it, and use the `url` inside the `SKIN` texture object.

> **Online-mode servers:** Mojang requires a cryptographic signature alongside texture values. For full online-mode skin support, integrate [SkinsRestorer](https://skinsrestorer.net) as a soft-depend alongside Shadow.

---

## ✦ Permissions

| Permission | Description | Default |
|---|---|---|
| `shadow.nick` | Use `/nick` | `true` |
| `shadow.realnick` | Use `/realnick` | `true` |
| `shadow.skin` | Use `/skin` | `true` |
| `shadow.role.relic` | Grants `✦︱Relic` prefix | `false` |
| `shadow.role.cursed` | Grants `✦︱Cursed` prefix | `false` |
| `shadow.role.dead` | Grants `✦︱Dead` prefix | `false` |

Role flair priority (highest wins): `relic > cursed > dead`.

---

## ✦ Commands

| Command | Description | Permission |
|---|---|---|
| `/nick <name>` | Set your nickname | `shadow.nick` |
| `/nick random` | Random nickname | `shadow.nick` |
| `/realnick` | See your own original name | `shadow.realnick` |
| `/realnick <player>` | See another player's original name | `shadow.realnick` |
| `/skin random` | Adopt a random SMP skin | `shadow.skin` |

**Nickname rules:**
- Max 16 characters
- Allowed characters: `a-z A-Z 0-9 & _ -`
- `&` color codes are parsed (e.g. `&6GoldBlade` → <span style="color:gold">GoldBlade</span>)

---

## ✦ Project Structure

```
Shadow/
├── pom.xml
├── plugin.yml
├── data/
│   ├── nicknames.json
│   ├── originals.json
│   └── skins.json
└── src/main/java/com/shadow/
    ├── Shadow.java            # Plugin entry point, version fetch, command registration
    ├── NickManager.java       # Core data manager — nicknames, originals, skins, persistence
    ├── NickCommand.java       # /nick handler
    ├── RealNickCommand.java   # /realnick handler
    └── SkinCommand.java       # /skin handler
```

---

## ✦ Contributing

Contributions are welcome from anyone — whether you're fixing a bug, adding a feature, or improving the skin pipeline.

### Getting Started

```bash
git clone https://github.com/your-org/Shadow.git
cd Shadow
mvn clean package
```

### Guidelines

- Follow the existing one-class-one-responsibility structure
- No comments inside code — use clear naming instead
- All user-facing messages must use the `✦︱` cinematic prefix style
- New commands must be registered in both `Shadow.java` and `plugin.yml`
- Test on a local PaperMC server before opening a PR
- Open an issue first for large feature additions so design can be discussed

### Pull Request Process

1. Fork the repository
2. Create a branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "feat: describe your change"`
4. Push and open a Pull Request against `main`
5. A maintainer will review within a few days

### Reporting Bugs

Open a [GitHub Issue](../../issues) with:
- Server software and version (e.g. Paper 1.21.4)
- Java version
- Steps to reproduce
- Full stack trace from `logs/latest.log` if applicable

---

## ✦ Roadmap

- [ ] `/nick reset` — revert to original Mojang username
- [ ] `/skin set <url>` — apply a skin from a direct URL
- [ ] Soft-depend on SkinsRestorer for online-mode skin support
- [ ] Tab-completion for `/realnick <player>`
- [ ] Admin command `/shadow reload` to hot-reload `skins.json`
- [ ] Per-role curated skin pools (Relic / Cursed / Dead pools in `skins.json`)
- [ ] Adventure API migration for full component-based formatting

---

## ✦ License

```
MIT License

Copyright (c) 2025 Twilight SMP

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## ✦ Acknowledgements
Built for the **Twilight SMP** community. Open-sourced for every SMP server that wants cinematic identity in their world.

> *"In the shadow of a name, legends are born."*
