# PDS4 JParser
Java library providing APIs for parsing and exporting information
on PDS4 table and image objects to various formats including CSV, PNG, Vicar, 
Fits, etc. The software is packaged in a JAR file.

# System Requirements
Current software requires:
* Java 1.8 - the Ant build does not appear to complete succcessfully in later versions of Java
* Maven 2 - EN builds have not yet migrated to Maven 3, so unknown if the software will build correctly

# Documentation
The documentation including release notes, installation and operation of the 
software should be online at https://nasa-pds-incubator.github.io/pds4-jparser/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

# Build
The software can be compiled and built with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 

In order to create a complete distribution package, execute the 
following commands: 

```
mvn site
mvn package
```

# Operational Release

A release candidate should be created after the community has determined that a release should occur. These steps should be followed when generating a release candidate and when completing the release.

## Update Version Numbers

Update pom.xml for the release version or use the Maven Versions Plugin, e.g.:
```
mvn versions:set -DnewVersion=$VERSION
```

## Update Changelog
Update Changelog using [ Changelog Generator](https://github.com/github-changelog-generator/github-changelog-generator). Note: Make sure you set `$CHANGELOG_GITHUB_TOKEN` in your `.bash_profile` or use the `--token` flag.
```
github_changelog_generator --future-release v$VERSION
```

## Commit Changes
Commit changes using following template commit message:
```
[RELEASE] PDS4 Java Parser Library v$VERSION
```

## Build and Deploy Software to [Sonatype Maven Repo](https://repo.maven.apache.org/maven2/gov/nasa/pds/).

```
mvn clean site deploy -P release
```

Note: If you have issues with GPG, be sure to make sure you've created your GPG key, sent to server, and have the following in your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <gpg.executable>gpg</gpg.executable>
      <gpg.keyname>KEY_NAME</gpg.keyname>
      <gpg.passphrase>KEY_PASSPHRASE</gpg.passphrase>
    </properties>
  </profile>
</profiles>

```

## Push Tagged Release
```
git tag v$VERSION
git push --tags
```

## Deploy Site to Github Pages

From cloned repo:
```
git checkout gh-pages
rsync -av target/site/* .
rm -fr target
git add .
git commit -m "Deploy v$VERSION docs"
git push origin gh-pages
```

## Update Versions For Development

Update `pom.xml` with the next SNAPSHOT version either manually or using Github Versions Plugin, e.g.:
```
VERSION=1.16.0-SNAPSHOT
mvn versions:set -DnewVersion=$VERSION
git add pom.xml
git commit -m "Update version for $VERSION development"
git push -u origin master
```

## Complete Release in Github
Currently the process to create more formal release notes and attach Assets is done manually through the [Github UI](https://github.com/NASA-PDS-Incubator/pds4-jparser/releases/new) but should eventually be automated via script.

# Snapshot Release
1. Checkout the dev branch.

2. Deploy software to Sonatype Maven repo:
```
mvn clean site deploy
```

# JAR Dependency Reference

## Official Releases
https://search.maven.org/search?q=g:gov.nasa.pds%20AND%20a:pds4-jparser&core=gav

## Snapshots
https://oss.sonatype.org/content/repositories/snapshots/gov/nasa/pds/pds4-jparser

If you want to access snapshots, add the following to your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
     <id>allow-snapshots</id>
     <activation><activeByDefault>true</activeByDefault></activation>
     <repositories>
       <repository>
         <id>snapshots-repo</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases><enabled>false</enabled></releases>
         <snapshots><enabled>true</enabled></snapshots>
       </repository>
     </repositories>
   </profile>
</profiles>
```
