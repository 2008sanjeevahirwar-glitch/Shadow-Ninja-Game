# Shadow Ninja: Trials of the Dojo

A Pygame-based ninja platformer game with stealth mechanics, double jump, dash attacks, and enemy AI.

## Features

🎮 **Gameplay Mechanics:**
- **Player Movement**: Left/Right movement, jumping, and double jump ability
- **Stealth Mode**: Press 'S' to become semi-transparent and avoid enemy detection
- **Dash Attack**: Press SHIFT to dash (unlocked after collecting 2 coins)
- **Wall Sliding**: Slide down walls and perform wall jumps for advanced movement
- **Combat**: Press 'F' to attack enemies
- **Coin Collection**: Collect coins to unlock the Dash skill

👾 **Enemy AI:**
- Enemies patrol left and right
- When player is in view cone (100 pixels), enemies chase
- Stealth mode makes player invisible to enemies
- Enemies have health system and can be defeated

⚙️ **Game Elements:**
- **Health System**: Start with 100 health
- **Coins**: 2 coins placed on level
- **Platforms**: 3 jumping platforms
- **Spikes/Traps**: Damage player on contact
- **Skill Unlock System**: Collect 2 coins to unlock Dash ability

## Controls

| Key | Action |
|-----|--------|
| ← → or A/D | Move Left/Right |
| SPACE | Jump |
| SPACE (Mid-air) | Double Jump |
| SHIFT | Dash Attack |
| S | Stealth Mode |
| F | Combat Attack |

## Installation & Requirements

```bash
pip install pygame
```

## How to Run

```bash
python shadow_ninja_game.py
```

## Game Window

- **Resolution**: 800x600 pixels
- **FPS**: 60
- **Title**: "Shadow Ninja: Trials of the Dojo"

## Color Legend

- 🔵 **Blue**: Player character
- 🔴 **Red**: Enemies and Spikes
- 🟢 **Green**: Coins
- ⚫ **Gray**: Platforms
- ⚪ **Black**: Background

## Game Statistics

UI displays:
- Current Health
- Coins collected
- Skill unlock status

## Author

Created as an engineering student project in Python/Pygame.

## License

Open source for educational purposes.

Enjoy the game! 🎮
