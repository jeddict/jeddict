<h1 align="center">
  <br>
  <a href="https://jeddict.github.io">
    <img src="https://jeddict.github.io/images/logo/logo.png" alt="Jeddict" width="150">
  </a>
  <br>
  Jeddict
  <br>
</h1>

<h4 align="center">Java EE 8 (Jakarta EE) & MicroProfile 2.0 application generator and modeler</h4>

<p align="center">
    <a href="https://opensource.org/licenses/Apache-2.0">
        <img src="https://img.shields.io/badge/license-Apache%202.0-green.svg" alt="Apache 2.0 License">
    </a>
    <a href="https://github.com/jeddict/jeddict/releases">
        <img src="https://img.shields.io/github/release/jeddict/jeddict/all.svg" alt="latest version">
    </a>
    <a href="https://opencollective.com/imjeddict">
        <img src="https://img.shields.io/badge/donate-$-orange.svg?maxAge=2592000&amp;style=flat">
    </a>
    <a href="https://www.youtube.com/imjeddict">
        <img src="https://img.shields.io/badge/youtube-ImJeddict-red.svg" alt="Demo">
    </a>
    <a href="https://twitter.com/intent/follow?screen_name=imjeddict">
        <img src="https://img.shields.io/twitter/follow/imjeddict.svg?style=social&logo=twitter" alt="follow on Twitter">
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


## Building from Source

You can build Jeddict using JDK 8 and test with NetBeans IDE 8.2 :

```bash
$ git clone https://github.com/jeddict/netbeans-modeler.git
$ mvn -f "netbeans-modeler" clean install
$ git clone https://github.com/jeddict/jeddict.git
$ mvn -f "jeddict" clean install
$ mvn -f "jeddict" nbm:run-ide -Dnetbeans.installation="C:\Program Files\NetBeans 8.2"
```
Note: `netbeans.installation` properties refer to the NetBeans IDE 8.2 path.

Optionally you may also build [jeddict-extensions](https://github.com/jeddict/jeddict-extensions)
and [hipee](https://github.com/jeddict/hipee) for out of the box features.


## Contributing

Please take a look at our [contributing](https://github.com/jeddict/jeddict/blob/master/CONTRIBUTING.md) guidelines if you're interested in helping!


## License

Jeddict is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).


#### If you like:heart: this project, don't forget:blush: to give us a star:star2: on GitHub!
