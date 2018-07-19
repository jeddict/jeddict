# Contributing to Jeddict

Are you ready to contribute to Jeddict? We'd love to have you on board, and we 
will help you as much as we can. Here are the guidelines we'd like you to follow
 so that we can be of more help:

 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Development setup](#setup)
 
 
## <a name="issue"></a> Issues and Bugs
If you find a bug in the source code or a mistake in the documentation, you can 
help us by submitting a ticket to our [GitHub  issues](https://github.com/jeddict/jeddict/issuess). Even better, you can submit a Pull Request to our [Jeddict project](https://github.com/jeddict/jeddict), [jeddict-extensions project](https://github.com/jeddict/jeddict-extensions) or to our [Documentation project](https://github.com/jeddict/jeddict.github.io).

**Please see the Submission Guidelines below**.

## <a name="feature"></a> Feature Requests
You can request a new feature by submitting a ticket to our [GitHub issues](https://github.com/jeddict/jeddict/issues). If you
would like to implement a new feature then consider what kind of change it is:

* **Major Changes** that you wish to contribute to the project should be 
discussed first. Please open a ticket which clearly states that it is a feature 
request in the title and explain clearly what you want to achieve in the 
description, and the Jeddict team will discuss with you what should be done in 
that ticket. You can then start working on a Pull Request.
* **Small Changes** can be proposed without any discussion. Open up a ticket 
which clearly states that it is a feature request in the title. Explain your 
change in the description, and you can propose a Pull Request straight away.

## <a name="submit"></a> Submission Guidelines

### Submitting an Issue
Before you submit your issue, search the archive, maybe your question was already answered.

If your issue appears to be a bug, and hasn't been reported, open a new issue.
Help us to maximize the effort we can spend fixing issues and adding new
features, by not reporting duplicate issues.  Providing the following information will increase the
chances of your issue being dealt with quickly:

* **Overview of the issue** - if an error is being thrown a stack trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **Jeddict Version(s) and Operating System**
* **Reproduce the error** - an unambiguous set of steps to reproduce the error. 
* **Related issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit)

Click [here](https://github.com/jeddict/jeddict/issuess/new) to open a bug issue.

### Submitting a Pull Request
Before you submit your pull request consider the following guidelines:

* If you want to modify the Jeddict, read our [Development setup](#setup)

* Make your changes in a new git branch

    ```shell
    git remote add upstream https://github.com/jeddict/jeddict
    git remote add origin https://github.com/username/jeddict
    git checkout -b my-fix-branch
    ```

* Once you have modified existing files or added new files to the project, you can add them to your local repository

    ```shell
    git add *
    ```
* Commit your changes using a descriptive commit message

    ```shell
    git commit -m "Fixed issue #101 - Title"
    ```

* Pull the the latest upstream changes

    ```shell
    git checkout master
    git pull upstream master
    ```

* Rebase and push your changes to GitHub:

    ```shell
    git checkout my-fix-branch
    git rebase master
    git push origin my-fix-branch:my-fix-branch
    ```

* Finally, you are ready to make a pull request to the original repository on GitHub

That's it! Thank you for your contribution!

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull 
the changes from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your 
local shell as follows:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the master branch:

    ```shell
    git checkout master -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your master with the latest upstream version:

    ```shell
    git pull --ff upstream master
    ```

## <a name="setup"></a> Development setup
Jeddict is using [netbeans-modeler](https://github.com/jeddict/netbeans-modeler),
 so you must add these dependencies in order to be able to run and test your changes.
Optionally you may also build [jeddict-extensions](https://github.com/jeddict/jeddict-extensions)
and [hipee] (https://github.com/jeddict/hipee) for out of box features.

### Fork the Jeddict project

Go to the [Jeddict project](https://github.com/jeddict/jeddict) and click on the
 "fork" button. You can then clone your own fork of the project, and start 
working on it.

[Please read the Github forking documentation for more information](https://help.github.com/articles/fork-a-repo)
