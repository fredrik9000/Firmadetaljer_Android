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

![selected_search_item_light](https://user-images.githubusercontent.com/13121494/64923215-0ba85600-d7d8-11e9-9632-b6e3973daeba.png)

![selected_search_item_dark](https://user-images.githubusercontent.com/13121494/64923216-0e0ab000-d7d8-11e9-8326-633ca8565aa6.png)
