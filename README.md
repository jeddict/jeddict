<h1 align="center">
  <br>
  <a href="https://jeddict.github.io">
    <img src="https://jeddict.github.io/images/logo/logo.png" alt="Jeddict" width="150">
  </a>
  <br>
  Jeddict
  <br>
</h1>

<h4 align="center">Jakarta EE 8 (Java EE) & MicroProfile 3.2 application generator and modeler</h4>

<p align="center">
    <a href="https://travis-ci.org/jeddict/jeddict">
        <img src="https://travis-ci.org/jeddict/jeddict.svg?branch=master" alt="Build status">
    </a>
    <a href="https://github.com/jeddict/jeddict/releases">
        <img src="https://img.shields.io/github/release/jeddict/jeddict/all.svg" alt="latest version">
    </a>
    <a href="https://opencollective.com/imjeddict">
        <img src="https://img.shields.io/badge/donate-$-orange.svg?maxAge=2592000&amp;style=flat">
    </a>
    <a href="https://twitter.com/imjeddict">
        <img src="https://img.shields.io/twitter/follow/imjeddict.svg?style=social&label=twitter&style=for-the-badge" alt="follow on Twitter">
    </a>
    <a href="https://www.youtube.com/imjeddict">
        <img src="https://img.shields.io/badge/youtube-ImJeddict-red.svg" alt="Demo">
    </a>
    <a href="https://opensource.org/licenses/Apache-2.0">
        <img src="https://img.shields.io/badge/license-Apache%202.0-green.svg" alt="Apache 2.0 License">
    </a>
    <a href='https://sourcespy.com/github/jeddictjeddict/' title='SourceSpy Dashboard'>
        <img src='https://sourcespy.com/shield.svg'/>
    </a>
</p>

## Key Features

##### 1) [JPA 2.2 source generation](http://jeddict.github.io/tutorial/page.html?l=QuickStart)
##### 2) Java EE 8 / MicroProfile full-stack application generator
 - ###### [Monolith](https://jeddict.github.io/page.html?l=tutorial/Monolith)
 - ###### [MicroService](https://jeddict.github.io/page.html?l=tutorial/MicroService)
##### 3) Visualize architecture
<p align="center">
<img src="https://jeddict.github.io/tutorial/Inheritance/INHERITANCE.PNG" width="400">
</p>

##### 4) Reverse Engineering of Java Source Code
##### 5) Visual creation of database structures
##### 6) JSONB modeler
##### 7) DB Reverse Engineering
##### 8) SQL/DDL script generation
##### 9) Database schema model viewer
<p align="center">
<img src="https://jeddict.github.io/tutorial/Inheritance/JOINED.PNG" width="500">
</p>

## Quick Start

See the [Quick Start](http://jeddict.github.io/tutorial/page.html?l=QuickStart) for demonstration, examples, and other information.

## Download

You can [download](https://jeddict.github.io/page.html?l=p/download) latest installable version of Jeddict.

See the [Manual Installation](https://jeddict.github.io/page.html?l=p/installation) and [Update Center Installation](https://jeddict.github.io/page.html?l=p/ucinstallation) for installation instructions.


## Getting help

- [GitHub Issues](https://github.com/jeddict/jeddict/issues)
- Google Group: [Jeddict](https://groups.google.com/forum/#!forum/jeddict)

## Stay Informed

- Twitter: [@ImJeddict](http://twitter.com/ImJeddict)


## Contributing

Please take a look at our [contributing](https://github.com/jeddict/jeddict/blob/master/CONTRIBUTING.md) guidelines if you're interested in helping!


## Build and run from Source

You can build Jeddict using JDK 8+ and test with NetBeans IDE 11.3 :

<!--( 
### Build Apache NetBeans IDE modules and deploy to a local repository
```bash
git clone -b 11.3 --single-branch --depth 1 https://github.com/apache/incubator-netbeans.git
ant -f incubator-netbeans -Dcluster.config=enterprise build-nozip build-nbms
mvn nb-repository:populate -DforcedVersion=RELEASE113 -DnetbeansInstallDirectory=incubator-netbeans/nbbuild/netbeans -DnetbeansNbmDirectory=incubator-netbeans/nbbuild/nbms
```
)--> 

### Build
```bash
git clone https://github.com/jeddict/netbeans-modeler.git
git clone https://github.com/jeddict/jeddict.git
git clone https://github.com/jeddict/jeddict-extensions.git
git clone https://github.com/jeddict/hipee.git
mvn -f "netbeans-modeler" clean install
mvn -f "jeddict" clean install
mvn -f "jeddict-extensions" clean install
mvn -f "hipee" clean install
```
### Run
```bash
mvn -f "jeddict" nbm:run-ide -Dnetbeans.installation=<path-to-netbeans-11.3-home-directory>
```
** `netbeans.installation` property can be added to maven settings.xml file.

### Test Suite
To setup the test suite first follow the above build instructions.

```bash
git clone https://github.com/jeddict/jeddict-test-suite.git
mvn -f "jeddict-test-suite" clean install -DskipTests

mvn -f "jeddict\tests" test
mvn -f "jeddict-test-suite" test
```

### Create nbm and cluster
```bash
mvn -f "netbeans-modeler" clean install nbm:nbm nbm:cluster nbm:autoupdate -Dnbm.build.dir=
mvn -f "jeddict" clean install nbm:nbm nbm:cluster nbm:autoupdate -P release -Dnbm.build.dir=
mvn -f "jeddict-extensions" clean install nbm:nbm nbm:cluster nbm:autoupdate -Dnbm.build.dir=
mvn -f "hipee" clean install nbm:nbm nbm:cluster nbm:autoupdate -Dnbm.build.dir=
```

Copy the nbm & clusters from `target\netbeans_site` & `target\netbeans_clusters`.


## License

Jeddict is Open Source [Jakarta EE](https://jakarta.ee/) application development platform released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).


#### If you like:heart: this project, don't forget:blush: to give us a star:star2: on GitHub!
