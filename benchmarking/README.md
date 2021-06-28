# Benchmarking

## Run plotting script manually
Optional: [Create a virtiual enviroment](https://wiki.ubuntuusers.de/virtualenv/)

Install requirements:
```bash
pip install -r requirements.txt
```

Run script:
```bash
python main.py
```

## Get data without db
scp \<name>@\<ip>:/etc/scripts/stats.json ./data/\<name>-\<state>.json
