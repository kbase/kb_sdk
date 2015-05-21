# file: mobu-completion
# Setup parameter completion for kb-mobu

_SetupMobuCompletion ()
{                 
    # setup the command names (this should probably be passed in and generated from the code)
    local COMPILE_CMD=compile
    local INIT_CMD=init
    local VALIDATE_CMD=validate
    local HELP_CMD=help

    local cur firstCmd prev

    cur=${COMP_WORDS[COMP_CWORD]}
    firstCmd=${COMP_WORDS[1]}
    prev=${COMP_WORDS[COMP_CWORD-1]}

    case ${COMP_CWORD} in
        1)
            # handle the first command
            COMPREPLY=($(compgen -W "${COMPILE_CMD} ${INIT_CMD} ${VALIDATE_CMD} ${HELP_CMD}" ${cur}))
            ;;
        *)
            case ${firstCmd} in
                ${COMPILE_CMD})
                    case ${prev} in
                      "--out") COMPREPLY=($(compgen -d -- "${cur}"));;
                      #"--js") COMPREPLY=();;
                      *)
                        local xpat='!*.spec'
                        COMPREPLY=(
                          $(compgen -W "--out" -- ${cur})
                          $(compgen -f -X "$xpat" -- "${cur}")
                          $(compgen -d -- "${cur}")
                          )
                        ;;
                    esac
                    ;;
                ${INIT_CMD})
                    # currently there are no init options
                    COMPREPLY=()
                    ;;
                ${VALIDATE_CMD})
                    case ${prev} in
                      # if options are added to the validate command that take parameters, they should be placed here
                      #"--verbose") COMPREPLY=();
                      *) COMPREPLY=($(compgen -W "-v --verbose" -- ${cur}) $(compgen -d -- "${cur}"));;
                    esac
                    ;;
                ${HELP_CMD})
                    case ${COMP_CWORD} in
                      2)
                        COMPREPLY=($(compgen -W "${COMPILE_CMD} ${INIT_CMD} ${VALDIDATE_CMD}" ${cur}))
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

complete -F _SetupMobuCompletion -o filenames kb-mobu