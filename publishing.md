# Publishing the library modules to a local NPM registry

## Getting started

We use [verdaccio](https://verdaccio.org/) as a local private NPM registry where 
we can publish the different library modules to be tested at a later time. 

#### Install verdaccio

Start by installing verdaccio globally:

```bash
npm i -g verdaccio
```

#### Registering a user

Next, we configure the user which is going to publish packages to the private registry:

```bash
npm adduser --registry http://localhost:4873
```

Then follow the prompts on the command line.

#### Run verdaccio

To start verdaccio, run the following command:

```bash
verdaccio
```

That's it. You are now ready to publish your packages to verdaccio.

## Publishing the library packages

On the root folder of the project, run the `publishLocal` npm script:

```bash
npm run publishLocal
```

This publishes all library packages to your local verdaccio repository.
