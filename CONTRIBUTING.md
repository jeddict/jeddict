# Contributing to JPA Modeler

Are you ready to contribute to JPA Modeler? We'd love to have you on board, and we will help you as much as we can. Here are the guidelines we'd like you to follow so that we can be of more help:

 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Generator development setup](#setup)
 - [Coding Rules](#rules)
 - [Git Commit Guidelines](#commit)
 
 ## <a name="issue"></a> Issues and Bugs
If you find a bug in the source code or a mistake in the documentation, you can help us by submitting a ticket to our [GitHub  issues](https://github.com/jGauravGupta/JPAModeler/issuess). Even better, you can submit a Pull Request to our [JPA Modeler project](https://github.com/jGauravGupta/JPAModeler), [jCode project](https://github.com/jGauravGupta/jCode) or to our [Documentation project](https://github.com/jpamodeler/jpamodeler.github.io).

**Please see the Submission Guidelines below**.

## <a name="feature"></a> Feature Requests
You can request a new feature by submitting a ticket to our [GitHub issues](https://github.com/jGauravGupta/JPAModeler/issues). If you
would like to implement a new feature then consider what kind of change it is:

* **Major Changes** that you wish to contribute to the project should be discussed first. Please open a ticket which clearly states that it is a feature request in the title and explain clearly what you want to achieve in the description, and the JPA Modeler team will discuss with you what should be done in that ticket. You can then start working on a Pull Request.
* **Small Changes** can be proposed without any discussion. Open up a ticket which clearly states that it is a feature request in the title. Explain your change in the description, and you can propose a Pull Request straight away.

## <a name="submit"></a> Submission Guidelines

### Submitting an Issue
Before you submit your issue search the archive, maybe your question was already answered.

If your issue appears to be a bug, and hasn't been reported, open a new issue.
Help us to maximize the effort we can spend fixing issues and adding new
features, by not reporting duplicate issues.  Providing the following information will increase the
chances of your issue being dealt with quickly:

* **Overview of the issue** - if an error is being thrown a stack trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **JPA Modeler Version(s) and Operating System**
* **Reproduce the error** - an unambiguous set of steps to reproduce the error. 
* **Related issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit)

Click [here](https://github.com/jGauravGupta/JPAModeler/issuess/new) to open a bug issue.

### Submitting a Pull Request
Before you submit your pull request consider the following guidelines:

* Search [GitHub](https://github.com/jGauravGupta/JPAModeler/pulls) for an open or closed Pull Request
  that relates to your submission.
* If you want to modify the JPA Modeler, read our [Development setup](#setup)
* Make your changes in a new git branch

     ```shell
     git checkout -b my-fix-branch master
     ```

* Create your patch
* Commit your changes using a descriptive commit message

     ```shell
     git commit -a
     ```

  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* Push your branch to GitHub:

    ```shell
    git push origin my-fix-branch
    ```

* In GitHub, send a pull request to `jGauravGupta/JPAModeler:master`.
* If we suggest changes then
  * Make the required updates.
  * Rebase your branch and force push to your GitHub repository (this will update your Pull Request):

    ```shell
    git rebase master -i
    git push -f
    ```

That's it! Thank you for your contribution!

#### Resolving merge conflicts ("This branch has conflicts that must be resolved")

Sometimes your PR will have merge conflicts with the upstream repository's master branch. There are several ways to solve this but if not done correctly this can end up as a true nightmare. So here is one method that works quite well.

* First, fetch the latest information from the master

    ```shell
    git fetch upstream
    ```

* Rebase your branch against the upstream/master

    ```shell
    git rebase upstream/master
    ```

* Git will stop rebasing at the first merge conflict and indicate which file is in conflict. Edit the file, resolve the conflict then

    ```shell
    git add <the file that was in conflict>
    git rebase --continue
    ```

* The rebase will continue up to the next conflict. Repeat the previous step until all files are merged and the rebase ends successfully.
* Force push to your GitHub repository (this will update your Pull Request)

    ```shell
    git push -f
    ```

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:

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
JPA Modeler is using [NBModeler](https://github.com/jGauravGupta/NBModeler) and [jCode](https://github.com/jGauravGupta/jCode), so you must add these depedencies in order to be able to run and test your changes.

### Fork the JPA Modeler project

Go to the [JPA Modeler project](https://github.com/jGauravGupta/JPAModeler) and click on the "fork" button. You can then clone your own fork of the project, and start working on it.

[Please read the Github forking documentation for more information](https://help.github.com/articles/fork-a-repo)
