# Quick-Share Addon for Litematica

A Fabric mod that adds quick-share functionality to Litematica schematics. Upload and share your builds instantly!

## 🎯 Features

- **One-Click Upload**: Upload litematic files to Choculaterie's server with a single click
- **Instant Sharing**: Get a short URL to share your builds with others
- **Clipboard Integration**: URLs are automatically copied to your clipboard
- **In-Game Notifications**: Get real-time feedback on upload status
- **Async Operations**: Non-blocking uploads that don't freeze your game

## 📋 Requirements

- Minecraft 1.21.11
- Fabric Loader >= 0.18.2
- Fabric API
- **Litematica** (required at runtime)
- **MaLiLib** (required by Litematica)

## 🚀 Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download and install [MaLiLib](https://modrinth.com/mod/malilib)
4. Download and install [Litematica](https://modrinth.com/mod/litematica)
5. Download Quick-Share Addon and place it in your mods folder
6. Launch Minecraft!

## 🎮 Usage

### Using the API (for developers)

```java
import com.choculaterie.integration.LitematicaIntegration;
import java.io.File;

// Upload and share a litematic file
File schematic = new File("path/to/your/schematic.litematic");
LitematicaIntegration.shareLitematicFile(schematic);
// Automatically shows messages and copies URL to clipboard!
```

### For End Users

Integration with Litematica's GUI is coming soon! You'll be able to:
- Click a "Quick Share" button in the schematic manager
- Right-click schematics in the browser to share them
- Auto-share when saving new schematics

## 🔧 Development

### Building from Source

```bash
git clone https://github.com/choculaterie/Quick-Share-Addon.git
cd Quick-Share-Addon/fabric-example-mod-1.21
./gradlew build
```

The built jar will be in `build/libs/`

### Adding Litematica Integration

See [LITEMATICA_INTEGRATION.md](LITEMATICA_INTEGRATION.md) for detailed instructions on how to:
- Add buttons to Litematica's GUI using Mixins
- Hook into Litematica's events
- Create custom GUI elements

### Project Structure

- `src/client/java/com/choculaterie/`
  - `network/` - Network communication layer
  - `models/` - Data models
  - `integration/` - Litematica integration helpers
  - `mixin/` - Mixin classes (to be added)

## 📡 API

The mod uploads to Choculaterie's API:
```
POST https://choculaterie.com/api/LitematicDownloaderModAPI/upload
```

Returns a JSON response with a `shortUrl` field.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under CC0-1.0 License.

## 🔗 Links

- [Choculaterie Website](https://choculaterie.com/)
- [GitHub Repository](https://github.com/choculaterie/Quick-Share-Addon)
- [Litematica Mod](https://modrinth.com/mod/litematica)

## ⚠️ Disclaimer

This is an addon for Litematica and requires it to function. Make sure you have Litematica and its dependencies installed.

---

Made with ❤️ by Choculaterie

