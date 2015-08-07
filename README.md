# Clever-Eclipse

Clever-Eclipse is an eclipse plugin for managing and pushing your project to clever-cloud.com.

### 1-Building with Maven

For building the plugin with maven it's easy. You just have to run
`mvn install`

### 2-Open in eclipse

For openning the plugin in eclipse it's a bit harder.

#### 2.1-Installing the eclipse plugins
1. Open `Help->Install New Software...`
2. In `Work with:` field select `--All Available Sites--`
3. Search and Install :
 * Eclipse RCP Plug-in Developer Resources
 * Eclipse e4 Tools Developer Resources

#### 2.2-Installing m2e integration plugin
1. Open `Window->Preferences->Maven->Discovery`
2. Click on `Open Catalog`
3. Search and Install :
 * `m2e connector for maven-dependency-plugin`
 * `Tycho Configurator`

#### 2.3-(Optional) Install e4tools plugin
1. Open `Help->Install New Software...`
2. Click `Add...` button
3. Add `https://dl.bintray.com/vogellacompany/e4tools/`
4. Select the spies you want
5. Install them