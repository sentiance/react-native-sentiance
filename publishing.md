# Publishing the library modules to a local NPM registry

## Getting started

We use [verdaccio](https://verdaccio.org/) as a local private NPM registry where 
we can publish the different library modules to be tested at a later time. 

Make sure to run `npm i` on the root folder of the project to install verdaccio 
and other dev-time dependencies. Then run the following command to start verdaccio, 
on a separate terminal tab (This should start verdaccio on port **4873** by default):

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
