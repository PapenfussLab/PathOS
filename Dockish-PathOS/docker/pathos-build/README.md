PathOS Build
============

## Available tags

- `latest`, `1`: Build environment for PathOS 1.x

## About this image

This image contains a build environment for the PathOS variant curation platform.

## How to use this image

For a one-off build, the only command line addition is a volume to store the built artifacts.

```bash
$ mkdir pathos
$ docker run --rm -it -v $(pwd)/pathos/:/pathos/ pathos-build
```

This will build the requisite artifacts and leave them in the
`pathos` directory.

The build process must download a large number of dependencies,
which takes a substantial amount of time. If repeated builds are
expected, the repeated downloading of dependencies may be avoided
by creating a *data* *container* to store the cached dependencies
before running the `pathos-build` container.

```bash
$ docker create -v /cache --name pathos-build-cache pathos-build
```

Then, to use the *data* *container*, we mention it when we run the build container:

```bash
$ docker run --rm -it -v $(pwd)/pathos/:/pathos/ --volumes-from pathos-build-cache pathos-build
```

### Environment Variables

If an alternate git repository is desired, the URL to it can be put
in the `PATHOS_GIT` environment variable.  This may require
authentication to be specified in the URL, though if the container
is run in interactive mode, credentials can be supplied. Alternitavely,
a volume may be used to overwrite `/root/.ssh/id_rsa` with an
alternative private key.

If the environment variable `PATHOS_BRANCH` is specified, the clone
of the repository will checkout the named branch before the build
is launched.

