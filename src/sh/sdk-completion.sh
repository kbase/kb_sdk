# file: sdk-completion.sh
# Setup parameter completion for kb-sdk

_SetupSDKCompletion ()
{                 
    # setup the command names (this should probably be passed in and generated from the code)
    local COMPILE_CMD=compile
    local INIT_CMD=init
    local VALIDATE_CMD=validate
    local HELP_CMD='help'
    local TEST_CMD='test'
    local VER_CMD=version

    local cur firstCmd prev

    cur=${COMP_WORDS[COMP_CWORD]}
    firstCmd=${COMP_WORDS[1]}
    prev=${COMP_WORDS[COMP_CWORD-1]}

    case ${COMP_CWORD} in
        1)
            # handle the first command
            COMPREPLY=($(compgen -W "${VER_CMD} ${INIT_CMD} ${VALIDATE_CMD} ${COMPILE_CMD} ${TEST_CMD} ${HELP_CMD}" ${cur}))
            ;;
        *)
            case ${firstCmd} in
                ${COMPILE_CMD})
                    case ${prev} in
                      # commands commented out here are just flags that can be set multiple times and take no args, so require
                      # no further filtering and we can show all the options again
                      "--out") COMPREPLY=($(compgen -d -- "${cur}"));;
                      #"--java") COMPREPLY=();;
                      #"--javabuildxml") COMPREPLY=();;
                      #"--javagwt") COMPREPLY=();;
                      "--javalib") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "--javapackage") COMPREPLY=();;
                      "--javasrc") COMPREPLY=($(compgen -d -- "${cur}"));;
                      #"--js") COMPREPLY=();;
                      "--jsclname") COMPREPLY=($(compgen -f -- "${cur}"));;
                      "--jsonschema") COMPREPLY=($(compgen -d -- "${cur}"));;
                      #"--makefile") COMPREPLY=();;
                      #"--pl") COMPREPLY=();;
                      "--plclname") COMPREPLY=();;
                      #"--plenableretries") COMPREPLY=();;
                      "--plimplname") COMPREPLY=();;
                      "--plpsginame") COMPREPLY=();;
                      #"--plsrv") COMPREPLY=();;
                      "--plsrvname") COMPREPLY=();;
                      #"--py") COMPREPLY=();;
                      "--pyclname") COMPREPLY=();;
                      "--pyimplname") COMPREPLY=();;
                      #"--pysrv") COMPREPLY=();;
                      "--pysrvname") COMPREPLY=();;
                      #"--r") COMPREPLY=();;
                      "--rclname") COMPREPLY=($(compgen -f -- "${cur}"));;
                      "--rimplname") COMPREPLY=($(compgen -f -- "${cur}"));;
                      #"--rsrv") COMPREPLY=();;
                      "--rsrvname") COMPREPLY=($(compgen -f -- "${cur}"));;
                      "--url") COMPREPLY=();;
                      *)
                        local xpat='!*.spec'
                        # ideally here we would detect which commands were already used and hide those
                        COMPREPLY=(
                          $(compgen -W "--java --javabuildxml --javagwt --javalib --javapackage --javasrc" -- ${cur})
                          $(compgen -W "--js --jsclname" -- ${cur})
                          $(compgen -W "--pl --plclname --plenableretries --plimplname --plpsginame --plsrv --plsrvname" -- ${cur})
                          $(compgen -W "--py --pyclname --pyimplname --pysrv --pysrvname" -- ${cur})
                          $(compgen -W "--r --rclname --rimplname --rsrv --rsrvname" -- ${cur})

                          $(compgen -W "--url" -- ${cur})
                          $(compgen -W "--makefile" -- ${cur})
                          $(compgen -W "--jsonschema" -- ${cur})
                          $(compgen -W "--out" -- ${cur})
                          $(compgen -f -X "$xpat" -- "${cur}")
                          $(compgen -d -- "${cur}")
                          )
                        ;;
                    esac
                    ;;
                ${INIT_CMD})
                    # init options
                    local INIT_OPTS='-e --example -l --language -u --user -v --verbose'
                    case ${prev} in
                      "-l") COMPREPLY=($(compgen -W "python perl java r" -- "${cur}"));;
                      "--language") COMPREPLY=($(compgen -W "python perl java r" -- "${cur}"));;
                      "-u") COMPREPLY=();;
                      "--user") COMPREPLY=();;
                      *)
                        # todo - check if options are in COMP_WORDS, if they are, don't show them again
                        COMPREPLY=($(compgen -W "${INIT_OPTS}" -- "${cur}") $(compgen -d -- "${cur}"))
                        ;;
                    esac
                    ;;
                ${VER_CMD})
                    # currently there are no init options
                    COMPREPLY=()
                    ;;
                ${TEST_CMD})
                     # test options
                    local METHOD_STORE_URLS='https://appdev.kbase.us/services/narrative_method_store/rpc'
                    case ${prev} in
                      # if options are added to the validate command that take parameters, they should be placed here
                      "-m") COMPREPLY=($(compgen -W "${METHOD_STORE_URLS}" -- "${cur}"));;
                      "--method_store") COMPREPLY=($(compgen -W "${METHOD_STORE_URLS}" -- "${cur}"));;
                      "-a") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "--allow_sync_method") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "-s") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "--skip_validation") COMPREPLY=($(compgen -d -- "${cur}"));;
                      *) COMPREPLY=($(compgen -W "-v --verbose -m --method_store -s --skip_validation" -- ${cur}) $(compgen -d -- "${cur}"));;
                    esac
                    ;;
                ${VALIDATE_CMD})
                    # note: something funky going on here with the ':' in the url
                    local METHOD_STORE_URLS='https://appdev.kbase.us/services/narrative_method_store/rpc'
                    case ${prev} in
                      # if options are added to the validate command that take parameters, they should be placed here
                      "-m") COMPREPLY=($(compgen -W "${METHOD_STORE_URLS}" -- "${cur}"));;
                      "--method_store") COMPREPLY=($(compgen -W "${METHOD_STORE_URLS}" -- "${cur}"));;
                      "-a") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "--allow_sync_method") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "-v") COMPREPLY=($(compgen -d -- "${cur}"));;
                      "--verbose") COMPREPLY=($(compgen -d -- "${cur}"));;
                      *) COMPREPLY=($(compgen -W "-v --verbose -m --method_store -a --allow_sync_method" -- ${cur}) $(compgen -d -- "${cur}"));;
                    esac
                    ;;
                ${HELP_CMD})
                    case ${COMP_CWORD} in
                      # purposely exclude the --all option because it makes auto complete a little more confusing
                      2)
                        COMPREPLY=($(compgen -W "${VER_CMD} ${INIT_CMD} ${VALIDATE_CMD} ${COMPILE_CMD} ${TEST_CMD}" ${cur}))
                        ;;
                      *)
                        COMPREPLY=()
                        ;;
                    esac
                    ;;
                *)
                    COMPREPLY=()
                    ;;
            esac
            ;;
    esac
  return 0
}

complete -F _SetupSDKCompletion -o filenames kb-mobu
complete -F _SetupSDKCompletion -o filenames kb-sdk