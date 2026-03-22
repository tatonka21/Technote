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

## Finding your GitHub email and name in Termux

If you are not sure which email address or display name is associated with
your GitHub account, run these commands in Termux **after** you have
authenticated:

```bash
# Show the GitHub username and email that gh is currently logged in with
gh auth status

# Show the name and email stored in your local git config
git config user.name
git config user.email
```

If `gh auth status` shows `not logged in`, authenticate first with the device
code flow above and then re-run the command.

If you have **not** set up a local git identity yet, you can configure it with:

```bash
git config --global user.name  "Your Name"
git config --global user.email "you@example.com"
```

Use the same email you registered with on GitHub so that your commits are
linked to your account.
