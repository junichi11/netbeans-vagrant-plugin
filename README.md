# NetBeans Vagrant Plugin

This plugin provides support for Vagrant.

## What's the Vagrant?

Please check the following site:

- http://www.vagrantup.com/

## Requirements

- NetBeans 7.3 or newer
- Vagrant 1.3.1 or newer

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
- ssh-config
- destroy
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

### Notice

Will take a little time when you access files or directories for your project at the first time.

## Troubleshooting

### VM state is "aborted"

Please try to start the virtual machine.

### Status display is wrong.

Please try to double-click on statusbar.

## Issues

If you hava some problems, please submit them to the github issue tracker.
(Please don't submit them to NetBeans bugzilla.)

## Version number

|       |stable |dev      |
|:------|:-----:|:-------:|
|pattern| n.n.n | n.n.n.n |
|e.g.   | 1.0.1 | 0.2.2.5 |

## Stable version

Available on Plugin Portal.

## Development version

Please add the following url:  
https://dl.dropboxusercontent.com/u/10953443/netbeans/vagrant-dev/7.3/updates.xml

### Notice

You should not use the development version except to test issues.

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)

