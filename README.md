# Firmadetaljer

This is an Android application where you can search for information about Norwegian companies. The search is done against the Brønnøysundsregistrene API: https://wiki.brreg.no/display/DBNPUB/API

The app is written in Kotlin, and has a modern MVVM architecture. The app uses the repository pattern and Android architecture components, such as:

- ViewModel
- Room
- LiveData
- Data Binding and View Binding
- Dagger-Hilt for dependency injection

This app does not use a single activity architecture, but my todo list app does: [Android Todo List](https://github.com/fredrik9000/TodoList_Android) 

#### The app has the following features:

- Search for companies by name, which can be filtered by number of employees
- Search for companies by organization number
- View company details
- View the company's location in Google Maps
- Navigate to parent company details (for those that have parent companies)
- Navigate to the company homepage
- Viewed companies are persisted and shown in its own list when not searching
- Option for clearing search history (meaning the persisted companies will be deleted)
- Dark mode, which can also be toggled from within the app

In order to view the company's location on a map you'll need to create your own Google Maps API key.

I have also made an iOS version: [iOS Firmadetaljer](https://github.com/fredrik9000/Firmadetaljer_iOS)

## Screenshots

![Screenshot_1568831706](https://user-images.githubusercontent.com/13121494/89735215-34c17d00-da61-11ea-82be-14d89e5fe5bc.png)

<p float="left">
  <img src="https://user-images.githubusercontent.com/13121494/89735214-3428e680-da61-11ea-9d67-fa60693ada93.png" width="412" />
  <img src="https://user-images.githubusercontent.com/13121494/89735213-33905000-da61-11ea-84be-a0236891b8b1.png" width="412" />
</p>

![Screenshot_1568831723](https://user-images.githubusercontent.com/13121494/89735217-34c17d00-da61-11ea-985d-2c878d7ce05a.png)

<p float="left">
  <img src="https://user-images.githubusercontent.com/13121494/89735367-61c25f80-da62-11ea-81d5-ab48a53f9520.png" width="412" />
  <img src="https://user-images.githubusercontent.com/13121494/89735437-c7aee700-da62-11ea-96c2-d6cf54dbd3c2.png" width="412" />
</p>
