# NetBeans Vagrant Plugin

This plugin provides support for Vagrant.

## What's the Vagrant?

Please check the following site:

- http://www.vagrantup.com/

## Requirements

- NetBeans 8.0 or newer
- Vagrant 1.6.0 or newer

## Install

Please download a nbm from Plugin Portal:  
[NetBeans Vagrant Plugin](http://plugins.netbeans.org/plugin/50630/vagrant)

## Usage

- set a vagrant path to Options
- add boxes
- select a **Project Node**
- run vagrant commands (e.g. up, init, suspend, e.t.c.)[1]

[1] There are three ways.

- Right-click project node > Vagrant
- Right-click Vagrant statusbar
- Tools > Vagrant

## Vagrant Root Settings

We can set Vagrant Root directory to project properties.
(Right-click project > properties > Vagrant)

Default (i.e. Vagrant Root field is empty) is project directory.

We can also set Vagrant Root with Init Action.

## Options

Tools > Options > Miscellaneous > Vagrant

![options](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-options.png)

- General : set a vagrant path
- Boxes : manage boxes
- Plugins : manage plugins

## Available commands with context menu action

![context menu](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-context-menu.png)

- up
- reload
- suspend
- resume
- halt
- init
- status
- share
- ssh
- ssh-config
- destroy
- provision
- box add
- plugin install
- run command

### Other commands

Please use `run command` action.

![run command](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-run-command.png)

## Boxes

![add box](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-add-box.png)

### add

We can use specific url for boxes. Its format is the same as vagrantbox.es.
If you want to use original url, please set it to Options.

e.g. https://gist.github.com/junichi11/6539855

If you want to use the gist(or github) url, please use raw data.
i.e. https://gist.github.com/junichi11/6539855/raw/831d375718f1954cd08d1da2c2a95705c6c36ef8/index.html

### remove

We can remove boxes at Vagrant Options.

## Plugins

![run command](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-install-plugin.png)

We can manage plugins on Options window.

### install

Show [available plugins](https://github.com/mitchellh/vagrant/wiki/Available-Vagrant-Plugins) list with Install pluign window.
Please select plugin, and click `OK`.

### uninstall

Plase select plugin name, and click `uninstall` button.

### update

Plase select plugin name, and click `update` button.

## Status Information

Vagrant status is displayed on the statusbar.If project root has a Vagrantfile, run `vagrant status`.
Otherwise, just display "not created". We can reload the status display if we double-click it.

![run command](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-statusbar.png)

### Note

Will take a little time when you access files or directories for your project at the first time.

## Syntax Highlight for Vagrantfile

![syntax highlight](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-syntax-highlight-vagrantfile.png)

Require Ruby plugin (http://plugins.netbeans.org/plugin/38549/ruby-and-rails)

## Action when project is closed

We can set an action when project is closed to the project properties.

- none : do nothing
- halt : run halt command
- halt (ask) : popup a question dialog, if you push `OK` button, project will be closed

We can also run this action when we close the NetBeans. If you set `halt (ask)`, cofirmation dialog is shown.

![halt confirmation dialog](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-closing-confirmation.png)

## Status management window

This window can show vagrant status of opened projects as list. You can also run commands (e.g. up, halt, ...) for a project.
Also show all machine status if you are using multiple machines.

If you want to reopen the window after you close it, please check `Windows > Vagrant Status`.

### Note

Please reboot NetBeans or reopen projects if status is not shown at the statusbar when you install this plugin at first.

![Vagrant statuses](https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant/screenshots/nb-vagrant-status-management-window.png)

## Troubleshooting

### VM state is "aborted"

Please try to start the virtual machine.

### Status display is wrong.

Please try to double-click on statusbar.

### sudo command problem

For example, in case of sudo command is used within vagrant plugins:
Probably, the following message will be shown *"sudo: no tty present and no askpass program specified"*

If passwd is visible, we can avoid this. **But this is not good.**
So, please run vagrant command with terminal if this message is shown.

### Vagrant could not detect VirtualBox!

If the below message is shown, please have a look at [#45](https://github.com/junichi11/netbeans-vagrant-plugin/issues/45).

```
/usr/bin/vagrant up
The provider 'virtualbox' that was requested to back the machine
'default' is reporting that it isn't usable on this system. The
reason is shown below:

Vagrant could not detect VirtualBox! Make sure VirtualBox is properly installed.
Vagrant uses the VBoxManage binary that ships with VirtualBox, and requires
this to be available on the PATH. If VirtualBox is installed, please find the
VBoxManage binary and add it to the PATH environmental variable.
Done.
```

## Issues

If you hava some problems, please submit them to the [GitHub issue tracker](https://github.com/junichi11/netbeans-vagrant-plugin/issues) .
(Please don't submit them to NetBeans bugzilla.)

## Version number

|       |stable |dev      |
|:------|:-----:|:-------:|
|pattern| n.n.n | n.n.n.n |
|e.g.   | 1.0.1 | 0.2.2.5 |

## Stable version

Available on Plugin Portal.

## Development version

I'll add new nbm for development to the following:  
https://github.com/junichi11/netbeans-vagrant-plugin/releases

### Note

You should not use the development version except to test issues.

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)

