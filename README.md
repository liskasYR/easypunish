<div align="center">
  <img src="icon.png" alt="EasyPunish" width="128">
  <h1>EasyPunish</h1>
  <p>A punishment management plugin built for Paper, Purpur, and Folia 1.21+ servers.</p>
</div>

---

## About EasyPunish

EasyPunish is a moderation plugin built to make server moderation fast and organized. Instead of dealing with messy commands and lost punishment times, EasyPunish gives every punishment a unique 6-digit ID, includes full GUI menus for staff, integrates directly with LuckPerms for voice-chat mutes, and lets players appeal their punishments directly in-game.

---

## Features

- **6-Digit Punishment IDs**: Every ban, mute, warn, and voice mute gets a unique 6-digit ID. If you unpunish someone early, the remaining time is saved, and you can resume it later using `/punish <ID>`.
- **Interactive Staff GUIs**: Open `/punish <player>` to pick reasons, categories (Chat, Ban, VoiceChat), and durations directly from a menu without typing long commands.
- **LuckPerms VoiceChat Mutes**: Hooks into LuckPerms temporary permissions to mute players in Simple Voice Chat smoothly.
- **Warning Thresholds**: Set how many warnings a player can get for specific reasons before the plugin automatically escalates it to a mute or ban.
- **In-Game Appeals (`/appeal` & `/appeals`)**: Punished players can submit an appeal with their punishment ID. Staff can open the appeals GUI to review, approve, or deny them on the spot.
- **Alt Account Detector (`/alts <player>`)**: Automatically links accounts joining from the same IP address and shows their current punishment status.
- **Custom GUI Sounds & Colors**: Includes clean sound effects on menu clicks and full hex color support across all messages.

---

## Commands and Permissions

| Command | Usage | Description | Permission |
|---|---|---|---|
| `/punish` | `/punish <player\|id> [type] [reason]` | Issue a punishment or open GUI | `easypunish.punish.chat / ban / voicechat` |
| `/unpunish` | `/unpunish <player\|id> [type]` | Remove a punishment or pause ID | `easypunish.unpunish.chat / ban` |
| `/warn` | `/warn <player> <type> <reason>` | Issue a warning | `easypunish.warn.chat / ban / voicechat` |
| `/unwarn` | `/unwarn <player> <type> <reason>` | Remove a warning | `easypunish.warn.chat / ban / voicechat` |
| `/kick` | `/kick <player> [reason]` | Kick a player | `easypunish.kick` |
| `/banlist` | `/banlist [player]` | Open active bans menu | `easypunish.banlist` |
| `/mutelist` | `/mutelist [player]` | Open active mutes menu | `easypunish.mutelist` |
| `/warnlist` | `/warnlist [player]` | Open active warnings menu | `easypunish.warnlist` |
| `/voicelist` | `/voicelist [player]` | Open active voice-mutes menu | `easypunish.voicelist` |
| `/history` | `/history <player>` | View a player's punishment history | `easypunish.history` |
| `/check` | `/check <player>` | Check active punishments in chat | `easypunish.admin` |
| `/alts` | `/alts <player>` | Check alt accounts linked to IP | `easypunish.alts` |
| `/appeal` | `/appeal <id> <reason>` | Submit a punishment appeal | `easypunish.appeal` |
| `/appeals` | `/appeals [id] [type]` | Open staff appeals menu | `easypunish.appeallist` |

---

## Configuration Files

- `config.yml` - Plugin messages and formatting.
- `permissions.yml` - Custom LuckPerms permission nodes.
- `punishments.yml` - Durations, punishment reasons, and warning thresholds.
- `gui.yml` - Menu titles, item layouts, and sounds.
- `data.yml` - Active punishments.
- `history.yml` - Punishment history logs.
- `appeals.yml` - Appeals database.
- `alts.yml` - Linked player IPs.
