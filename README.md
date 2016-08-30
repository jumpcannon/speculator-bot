# 🤔 speculator

A robot that speculates about things

## Usage

```
lein run [OPTION]
  -d, --dry-run     Just print to console, don't tweet
  -o, --oauth FILE  Location of oauth credential file
  -h, --help
```


Where FILE is a path to a file containing your oauth credentials,
in the following format:
```
    app-key
    app-secret
    user-key
    user-secret
```

### Bugs

It doesn't know how to pluralize nouns with special pluralization rules, so you might find it talking about "childs" or "gooses".

## License

Copyright © 2014-2016 Morgan Astra

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
