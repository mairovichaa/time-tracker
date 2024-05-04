### Overview

http://mairovichaa-time-tracker.s3-website.eu-central-1.amazonaws.com/

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

### IDEA
Project contains IDEA's configuration for local run: <i>time_tracker.Launcher</i>.

### Configuration

Example of [property file](desktop/src/main/resources/application.yml).

### CD

To trigger build and deployment of new version one need to 
1. Update app version in [build.gradle](build.gradle).
```groovy
...
subprojects {
   ...
   version = '1.1.9'
   ...
}
...
```
2. create a tag:<br> `git tag 1.1.9 -m "Release 1.1.9"`
3. push it to the repo: <br> 
`git push origin 1.1.9`

Then github action will start, which will 
1. Build app for different OSs
2. Create static web page with information about different versions (including the created one) of app. [CHANGELOG.md](CHANGELOG.md) and [index-template.html](other%2Fs3%2Findex-template.html) are used.
3. Upload artifacts to S3 bucket.

There is  [tag-recreate.sh](other%2Ftag-recreate.sh), which will delete existing tag (both locally and remotely), create new one with the same message and push it to remote.

```bash
> ./other/tag-recreate.sh 1.1.9

Extracting message from tag '1.1.9'
Extracted message 'Release 1.1.9'
Deleting tag from remote 'origin'
To https://github.com/mairovichaa/time-tracker.git
 - [deleted]         1.1.9
Deleting tag locally
Deleted tag '1.1.9' (was 235232e)
Creating tag locally
Pushing tag to remote 'origin'
Enumerating objects: 1, done.
Counting objects: 100% (1/1), done.
Writing objects: 100% (1/1), 165 bytes | 165.00 KiB/s, done.
Total 1 (delta 0), reused 0 (delta 0), pack-reused 0
To https://github.com/mairovichaa/time-tracker.git
 * [new tag]         1.1.9 -> 1.1.9
Tag 1.1.9 has been successfully recreated with its original message and pushed to origin
```