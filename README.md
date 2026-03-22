# Technote
This is an ai note app, a knowledge base, a app builder chat agent with github integrations, monaco, previews for builds, and more

Mockups:
- Page 1 — Authentication (dark theme login / sign up)
- Page 2 — Workspace dashboard (recent work + agent activity)
- Page 3 — Multi-agent planner (agent-per-page build + approval flow)
- Page 4 — Note detail + agent collaboration (inline agent chat, linked context & smart suggestions)
- Page 5 — GitHub integration (device-flow OAuth — works in Termux & headless terminals)

## GitHub authentication in Termux

The standard `gh auth login` command opens a browser window for OAuth, which
does not work in [Termux](https://termux.dev/) or other headless Android
terminals.  Use the **device code** flow instead:

```bash
# In Termux (or any terminal without a browser)
gh auth login --git-protocol https
```

`gh` will print a short code (e.g. `ABCD-1234`) and a URL
(`github.com/login/device`).  Open that URL on **any** device
(phone browser, desktop, etc.), enter the code, approve the app, and
authentication completes automatically — no browser on the terminal required.

The same device-flow is available inside the Technote app via
**Settings → Connected accounts → GitHub → Device code**.
