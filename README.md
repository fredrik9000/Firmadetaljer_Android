# Firmadetaljer

This is an Android application where you can search for information about Norwegian companies. The search is done against the Brønnøysundsregistrene API: https://confluence.brreg.no/display/DBNPUB/API 

The app is written in Kotlin and has an MVVM architecture using the repository pattern and with Android architecture components, such as:

- ViewModel
- Room
- LiveData
- Data Binding

This app does not use a single activity architecture or the navigation component, like my todo list app: [Android Todo List](https://github.com/fredrik9000/TodoList_Android) 

The app is in Norwegian only.

**The app has the following features:**

- Search for companies by name
- Search for companies by organization number
- View company details
- Navigate to parent company details (for those that have parent companies)
- Navigate to the company homepage
- Viewed companies are persisted and shown in its own list when not searching
- Option for clearing search history (meaning the persisted companies will be deleted)
- Dark mode, which can also be toggled from within the app


## Screenshots

![Screenshot_1568562492](https://user-images.githubusercontent.com/13121494/64924115-3519af80-d7e1-11e9-808c-8e9a9bcc7351.png)

![Screenshot_1568562503](https://user-images.githubusercontent.com/13121494/64924116-38ad3680-d7e1-11e9-8234-47a28f03c325.png)
