## 🧠 BoshyRPC — Discord Rich Presence for *I Wanna Be The Boshy*

This is a small project that shows your current in-game room, death count, and difficulty in Discord while you're playing *I Wanna Be The Boshy*.  
It updates automatically and stops when the game closes.

Made mostly for fun, took ~3 hours. Don't expect regular updates 😅

---

### 💡 Features

- Shows your current room (frame)
- Reads death count and difficulty from the save file
- Live updates to your Discord status every second or two
- Closes itself when the game is closed

---

### ⚙️ How to Use

1. Make sure you have **Java 8+** installed  
2. Launch **Discord**
3. Run `Main.java` (or a compiled `.jar`)
4. Start *I Wanna Be The Boshy*
5. That’s it — your Discord status will update automatically

---

### 👀 Example Discord Status

![BoshyRPC Preview](https://github.com/AkyAwens/BoshyRPC/blob/91d77afced21126a3b9b85ef8729bd7b29bf7738/assets/showcase.png)

---

### ⚠ Notes

- Only works on **Windows** (needs to read game memory)
- Discord must be running
- You’ll need to set up a Discord Application with the right image assets (`boshy`, `dark_boshy`) if you want pretty icons

---

### 📁 How it Works

- Watches for `Boshy.exe` to start
- Reads memory to get the current frame number (aka room name)
- Loads the latest modified `.ini` save file from the game directory
- Decrypts it using RC4 with key `"BLOB"` to get:
  - `Deaths`
  - `Difficulty`

---

### 🤷 Why?

Why not?  

---

### 🙌 Credits

- Made by [@AkyAwen](https://github.com/akyawen)  
- [@ACrowIAm](https://github.com/ACrowIAm/) for the [IWBTB Save Editor](https://github.com/ACrowIAm/I-Wanna-Be-The-Boshy-Save-Editor) program and explaining all the frames in the game.
- Uses [discord-rpc (Java wrapper)](https://github.com/MinnDevelopment/discord-rpc)

---

### 📜 License

MIT. Use, fork, or break it however you want.
