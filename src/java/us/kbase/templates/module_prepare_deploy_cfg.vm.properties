import sys
from jinja2 import Template
from ConfigParser import ConfigParser

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: <program> <deploy_cfg_template_file> <file_with_properties>")
        print("Properties from <file_with_properties> will be applied to <deploy_cfg_template_file>")
        print("template which will be overwritten with .orig copy saved in the same folder first.")
        sys.exit(1)
    file = open(sys.argv[1], 'r')
    text = file.read()
    t = Template(text)
    config = ConfigParser()
    config.read(sys.argv[2])
    props = dict(config.items("global"))
    output = t.render(props)
    with open(sys.argv[1] + ".orig", 'w') as f:
        f.write(text)
    with open(sys.argv[1], 'w') as f:
        f.write(output)
