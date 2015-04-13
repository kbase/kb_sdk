

1) install phantomjs

sudo apt-get install phantomjs

2) install casperjs

git clone git://github.com/n1k0/casperjs.git
cd casperjs
sudo ln -sf `pwd`/bin/casperjs /usr/local/bin/casperjs

3) start a test server (the example test config uses the example server in the perl test script director)

4) run the test script using casperjs

casperjs test test-client.js --jq=[path_to_jquery_lib] --tests=[test_cfg_file] --endpoint=[server_url] --token=[token]


