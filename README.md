# Technote
This is an ai note app, a knowledge base, a app builder chat agent with github integrations, monaco, previews for builds, and more

Mockups:
- Page 1 — Authentication (dark theme login / sign up / **GitHub Device Flow**)
- Page 2 — Workspace dashboard (recent work + agent activity)
- Page 3 — Multi-agent planner (agent-per-page build + approval flow)
- Page 4 — Note detail + agent collaboration (inline agent chat, linked context & smart suggestions)

## GitHub Authentication in Termux / headless environments

If you are running on **Termux on Android** (or any environment where a browser cannot be opened automatically), use the **GitHub Device Flow** instead of the standard `gh auth login` web flow.

### How it works

1. Tap **GitHub** on the login screen — the app requests a one-time code from GitHub.
2. The screen shows a short code (e.g. `ABCD-1234`) and the URL **https://github.com/login/device**.
3. Open that URL on **any** device that has a browser (your laptop, another phone, etc.).
4. Enter the code shown in the app and click **Authorize**.
5. The app detects the authorisation automatically and signs you in.

No browser is needed on the device running Technote / Termux.
