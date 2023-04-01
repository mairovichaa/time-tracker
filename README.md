### Requirements

- Java 11

### Run application
Suggested way to run app:

1. Download application: <b>TODO add link to place where it could be downloaded</b>.

2. Create folder, let's call it `APP_FOLDER`. 

3. Copy the downloaded jar to `APP_FOLDER`.

4. Create `data` folder inside `APP_FOLDER`.

5. Create property file in `APP_FOLDER`: `touch application.yml`.

   Below you could find example of such file.

   Use `<APP_FOLDER>/data` as value of `stopwatch.folderWithData` property.

6. Run 

```bash
cd <APP_FOLDER> && \
     pathToPropertiesFile=<path-to-property-file> \
     java -jar time-tracker-<time-tracker-version>-SNAPSHOT.jar
```

#### IDEA
Project contains IDEA's configuration for local run: <i>time_tracker.Launcher</i>.

### Configuration

Example of property file:
```yaml
stopwatch:
  dates:
    amountOfDaysToShow: 365
  devMode: false
  folderWithData: <replace-with-folder-where-you-want-to-store-data>
  defaultRecords:
    - record name 1
    - record name 2
    - record name 3
```