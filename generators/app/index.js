'use strict';
var yeoman = require('yeoman-generator');
var chalk = require('chalk');
var _ = require('lodash');
var pascalCase = require('pascal-case');
var path = require('path');

var SpringbootGenerator = module.exports = yeoman.generators.Base.extend({

    initializing: function () {
        this.appname = _.kebabCase(path.basename(process.cwd()));
    },

    prompting: function () {
        var done = this.async();

        // Have Yeoman greet the user.
        var logo = "  _   _       ______ _____             \n" +
            " | \\ | |     |  ____|  __ \\            \n" +
            " |  \\| | ___ | |__  | |  | | _____   __\n" +
            " | . ` |/ _ \\|  __| | |  | |/ _ \\ \\ / /\n" +
            " | |\\  | (_) | |    | |__| |  __/\\ V / \n" +
            " |_| \\_|\\___/|_|    |_____/ \\___| \\_/  \n" +
            "                                       \n";
        this.log(chalk.green(logo) + 'Welcome to the supreme ' + chalk.red('generator-yintai-springboot') + ' generator!' + '\n' + chalk.yellow('Usually the default prompt is recommended. '));

        var prompts = [
            {
                type: 'string',
                name: 'organizationName',
                message: '(1/11) What is the organization\'s name of service?',
                default: 'yintai'
            },
            {
                type: 'string',
                name: 'dockerPrefix',
                message: '(2/11) What is your Docker prefix?',
                default: function (response) {
                    return 'com.' + response.organizationName;
                }
            },
            {
                type: 'string',
                name: 'extraMavenRepo',
                message: '(3/11) What private maven repository would you like to use? (eg. https://nexus.yintai.org/public)'
            },
            {
                type: 'string',
                name: 'authorName',
                message: '(4/11) What is the author\'s name of service?',
                default: this.user.git.name()
            },
            {
                type: 'string',
                name: 'authorEmail',
                message: '(5/11) What is the author\'s email of service?',
                default: this.user.git.email()
            },
            {
                type: 'string',
                name: 'baseName',
                message: '(6/11) What is the base name of service?',
                default: this.appname
            },
            {
                type: 'string',
                name: 'packageName',
                message: '(7/11) What is the package name of service?',
                default: function (response) {
                    return 'com.' + response.organizationName + '.' + response.baseName.replace(/\-/g, '');
                }
            },
            {
                type: 'string',
                name: 'description',
                message: '(8/11) What is the description of service?'
            },
            {
                type: 'string',
                name: 'springBootVersion',
                message: '(9/11) What version of Spring Boot would you like to use?',
                default: '1.3.1.RELEASE'
            },
            {
                type: 'checkbox',
                name: 'dependencies',
                message: '(10/11) Select your dependencies.',
                choices: [
                    {
                        name: 'Jetty (Tomcat will be excluded)',
                        value: 'jetty',
                        checked: 'true'
                    },
                    {
                        name: 'Actuator',
                        value: 'actuator',
                        checked: 'true'
                    },
                    {
                        name: 'Data-jpa',
                        value: 'jpa'
                    },
                    {
                        name: 'NoFDev-RPC',
                        value: 'rpc',
                        checked: 'true'
                    }
                ]
            },
            {
                when: function (response) {
                    return response.dependencies.indexOf('jpa') != -1;
                },
                type: 'list',
                name: 'databaseType',
                message: 'Which *type* of database would you like to use? TODO: MongoDB not supported',
                choices: [
                    {
                        value: 'none',
                        name: 'None'
                    },
                    {
                        value: 'sql',
                        name: 'SQL (H2 & PostgreSQL)'
                    },
                    {
                        value: 'mongodb',
                        name: 'MongoDB (TODO: Not supported)'
                    }
                ],
                default: 'sql'
            },
            {
                type: 'confirm',
                name: 'hasSample',
                message: '(11/11) Would you like to contains a sample?',
                default: true
            }
        ];

        this.prompt(prompts, function (props) {
            // To access props later use this.props.someOption;
            this.props = props;
            this.props.applicationName = pascalCase(this.appname) + 'Application';
            var hasDependencies = function (dependency) {
                return props.dependencies.indexOf(dependency) !== -1;
            };
            this.props.jetty = hasDependencies("jetty");
            this.props.actuator = hasDependencies("actuator");
            this.props.jpa = hasDependencies("jpa");
            this.props.sql = (this.props.databaseType == 'sql');
            this.props.rpc = hasDependencies("rpc");
            if (this.props.hasSample) {
                this.props.rpc = this.props.hasSample;
            }
            this.log(this.props.rpc);
            done();
        }.bind(this));
    },

    writing: function () {
        var sourceDir = "src/main/groovy/";
        var resourcesDir = "src/main/resources/";
        var testDir = "src/test/groovy/";
        var dockerDir = "src/main/docker/";
        var packageDir = this.props.packageName.replace(/\./g, '/') + '/';
        var sampleDir = sourceDir + "com/yintai/sample/";
        var sampleDestDir = sourceDir + packageDir + "sample/";
        var sampleTestDir = testDir + "com/yintai/sample/";
        var sampleDestTestDir = testDir + packageDir + "sample/";

        //gradle
        this.template('build.gradle', 'build.gradle', this.props, {'interpolate': /<%=([\s\S]+?)%>/g});
        this.fs.copy(this.templatePath('gradlew'), this.destinationPath('gradlew'));
        this.fs.copy(this.templatePath('gradlew.bat'), this.destinationPath('gradlew.bat'));
        this.fs.copy(this.templatePath('gradle/wrapper/gradle-wrapper.jar'), this.destinationPath('gradle/wrapper/gradle-wrapper.jar'));
        this.fs.copy(this.templatePath('gradle/wrapper/gradle-wrapper.properties'), this.destinationPath('gradle/wrapper/gradle-wrapper.properties'));

        //app
        this.template(sourceDir + 'com/yintai/Application.groovy', sourceDir + packageDir + this.props.applicationName + ".groovy", this.props, {'interpolate': /<%=([\s\S]+?)%>/g});

        //resources
        this.template(resourcesDir + 'application.yml', resourcesDir + 'application.yml', this.props, {'interpolate': /<%=([\s\S]+?)%>/g});
        if (this.props.jetty) {
            this.fs.copy(this.templatePath(resourcesDir + 'keystore.jks'), this.destinationPath(resourcesDir + 'keystore.jks'));
        }
        //TODO test

        //docker
        this.template(dockerDir + "Dockerfile", dockerDir + "Dockerfile", this.props, {'interpolate': /<%=([\s\S]+?)%>/g});

        //readme
        this.template('README.md', 'README.md', this.props, {'interpolate': /<%=([\s\S]+?)%>/g});

        //git
        this.fs.copy(this.templatePath('gitignore'), this.destinationPath('.gitignore'));

        //sample
        this.template(sampleDir + "ConnectionConfiguration.groovy", sampleDestDir + "ConnectionConfiguration.groovy", this.props);
        this.template(sampleDir + "ConnectionSettings.groovy", sampleDestDir + "ConnectionSettings.groovy", this.props);
        this.template(sampleDir + "ProxyConfiguration.groovy", sampleDestDir + "ProxyConfiguration.groovy", this.props);
        this.template(sampleDir + "ProxySettings.groovy", sampleDestDir + "ProxySettings.groovy", this.props);
        this.template(sampleDir + "ProxyUrl.groovy", sampleDestDir + "ProxyUrl.groovy", this.props);
        this.template(sampleDir + "UserCriteria.groovy", sampleDestDir + "UserCriteria.groovy", this.props);
        this.template(sampleDir + "UserDTO.groovy", sampleDestDir + "UserDTO.groovy", this.props);
        this.template(sampleDir + "UserFacade.groovy", sampleDestDir + "UserFacade.groovy", this.props);
        this.template(sampleDir + "UserFacadeImpl.groovy", sampleDestDir + "UserFacadeImpl.groovy", this.props);
        this.template(sampleDir + "UserService.groovy", sampleDestDir + "UserService.groovy", this.props);
        this.template(sampleTestDir + "UserFacadeSpec.groovy", sampleDestTestDir + "UserFacadeSpec.groovy", this.props);
    },

    install: function () {
        // this.installDependencies();
    }
});
