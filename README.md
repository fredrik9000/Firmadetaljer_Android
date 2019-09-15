# Firmadetaljer

This is an Android application where you can search for information about Norwegian companies. The search is done against the Brønnøysundsregistrene API: https://confluence.brreg.no/display/DBNPUB/API 

The app is written in Kotlin and has an MVVM architecture using the repository pattern and Android architecture components, such as:

- ViewModel
- Room
- LiveData
- Data Binding

This app does not use a single activity architecture or the navigation component, but my todo list app does: [Android Todo List](https://github.com/fredrik9000/TodoList_Android) 

The app is in Norwegian only (unlike the iOS version which also supports English). The reason for this is that the API returns Norwegian data, and it's a Norway tailored app anyway. I might still translate the app to English in the future, as there is a benefit to that. You can find the iOS version here: [iOS Firmadetaljer](https://github.com/fredrik9000/Firmadetaljer_iOS)

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
