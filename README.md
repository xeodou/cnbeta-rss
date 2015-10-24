### cnbeta-rss

This Project show how is possible use [golang](https://golang.org) build a commnon library
for a Mobile Project.

#### Build it

* Build the package for each platform
  * ANDROID_HOME=you_SDK_path gomobile bind -target=android -o android/app/libs/cb.aar -v cnbeta
  * ANDROID_HOME=you_SDK_path gomobile bind -target=iOS -o ios/cb.framework -v cnbeta

* Build each application for each platform


#### Screen Shot
![screenshot](https://cloud.githubusercontent.com/assets/914595/10710047/17a98ab2-7a78-11e5-89bf-787f92e8a636.png)

#### Isuue
* This is still some issue with android platform, it doesn't work on My Moto x 1st.


#### TO DO
* Automatic everything

#### License
MIT
