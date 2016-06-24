# JRMP - JIRA Risk Management Plugin <img src="https://api.travis-ci.org/Augustyn/JRMP.svg?branch=master" alt="Build Status" style="max-width:100%;">

Atlassian JIRA plugin for controlling project risks.

JIRA Risk Management Plugin was created by students of Technical University of Lodz, with the cooperation of AMG.net S.A.
as a part of University classes. It's aim was to show companies tools, work practices by the way of working by the project.

# BUILDING:
Before building JRMP, please remove or comment <excludes/> from pom.xml:
```
<exclusions>
    <exclusion>
        <groupId>jta</groupId>
        <artifactId>jta</artifactId>
    </exclusion>
    <exclusion>
        <groupId>jndi</groupId>
        <artifactId>jndi</artifactId>
    </exclusion>
</exclusions>
```
Those artifacts were commented because they cannot be found on public maven repositories, thus their lack, cause travis
to fail builds. You need to remove this exclusion.
Required artifacts can be found with [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/downloads)
There are two ways you can build plugins by yourself:
1.
  - download [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/downloads)
  - install it
  - use alias/command 'atlas-mvn package' to build plugin
2.
  - download [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/downloads)
  - unpack or install it.
  - copy ATLASSIAN_SDK/repository/jta/jta to your $HOME/.m2/repository/jta/jta
  - copy ATLASSIAN_SDK/repository/jndi/jndi to your $HOME/.m2/repository/jndi/jndi
  - build as other maven project with:
  `mvn package`
