# Firmadetaljer

This is an Android application where you can search for information about Norwegian companies. The search is done against the Brønnøysundsregistrene API: https://wiki.brreg.no/display/DBNPUB/API

The app is written in Kotlin, uses Dagger 2, and has an MVVM architecture using the repository pattern and Android architecture components, such as:

- ViewModel
- Room
- LiveData
- Data Binding

This app does not use a single activity architecture or the navigation component, but my todo list app does: [Android Todo List](https://github.com/fredrik9000/TodoList_Android) 

The app is in Norwegian only (unlike the iOS version which also supports English). The reason for this is that the API returns Norwegian data, and it's a Norway tailored app anyway. I might still translate the app to English in the future, as there is a benefit to that. You can find the iOS version here: [iOS Firmadetaljer](https://github.com/fredrik9000/Firmadetaljer_iOS)

#### The app has the following features:

- Search for companies by name, which can be filtered by number of employees
- Search for companies by organization number
- View company details
- View the company's location in Google Maps (you will get an authorization failure, since the API key is kept privately. You need to create your own API key.)
- Navigate to parent company details (for those that have parent companies)
- Navigate to the company homepage
- Viewed companies are persisted and shown in its own list when not searching
- Option for clearing search history (meaning the persisted companies will be deleted)
- Dark mode, which can also be toggled from within the app

#### Potential improvements:

- Add a way to update saved companies.
- The maximum number of companies returned by the API is 100. Add pagination behaviour so that more data is retrieved as the user scrolls.
- (Adaptive) launcher icons.

## Screenshots

![Screenshot_1568831706](https://user-images.githubusercontent.com/13121494/65176250-b167f900-da54-11e9-8973-5e085ac354cc.png)

![Screenshot_1568831723](https://user-images.githubusercontent.com/13121494/65176255-b4fb8000-da54-11e9-9ba2-c8a32c777a1b.png)

![saved_companies_phone_dark](https://user-images.githubusercontent.com/13121494/65176135-75cd2f00-da54-11e9-9cb8-6ae2a401947c.png)

![company_details_phone_dark](https://user-images.githubusercontent.com/13121494/65176140-78c81f80-da54-11e9-8814-f5347049031d.png)
