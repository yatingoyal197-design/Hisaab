# Hisaab – Android expense tracker

Track money spent and received in rupees: add, edit and delete entries, see the monthly total,
a category breakdown and an optional monthly budget. Everything is stored on the phone.

**Bank SMS sync:** add your bank name and the last 4 digits of your account. Hisaab reads your bank's
debit and credit SMS on the phone and adds them automatically each time you open the app.
Messages never leave the phone. OTPs, reminders and failed transactions are ignored.
If one payment arrives in two SMS, the reference number (UPI Ref / RRN / UTR) keeps only one.

**Naming:** bank entries come in marked "Add name". Use "Name your bank payments" on Home to name them
one by one; Hisaab remembers the name, category and friend for that payee next time.

**Friends:** add friends, link any entry to a friend, and the Friends tab shows how much you sent,
received, and who owes whom. **Transactions** tab lists everything with search; tap any entry to see
full details including the original bank SMS.

## Get the app (no coding needed)
1. Create a free account at github.com and make a new repository (any name).
2. On the repository page choose **Add file → Upload files**, drag in everything
   inside this folder (including the hidden `.github` folder), then **Commit**.
3. Open the **Actions** tab. The "Build Hisaab app" run takes about 3–5 minutes.
4. Open the finished run, scroll to **Artifacts**, download **Hisaab-app** (a zip),
   and unzip it on your phone to get `Hisaab.apk`.
5. Tap `Hisaab.apk` and allow "Install unknown apps" when asked.

## Turning on SMS access
Open Hisaab → **Bank SMS sync** → add your account → **Allow SMS access**.
On Android 13 and newer, apps installed from a file are blocked from SMS access at first. If you see
"Restricted setting": open **App info** for Hisaab, tap ⋮ at the top right, choose
**Allow restricted settings**, then go back and tap **Allow SMS access** again.

To change the app, edit `app/src/main/assets/index.html` and upload again; GitHub rebuilds it.
