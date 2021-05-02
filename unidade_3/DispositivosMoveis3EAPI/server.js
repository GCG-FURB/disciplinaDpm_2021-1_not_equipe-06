const express = require('express');

const app = express();

const temperatura = [
    {
        nome: 'Blumenau',
        temp: 26,
        desc: 'Agradavel'
    },
    {
        nome: 'Urubici',
        temp: 0,
        desc: 'Frio'
    }
]

app.use(express.json());
//app.use(cors());

//require('./src/routes/index')(app);
app.get('/', function (req, res) {
    res.send(temperatura);
})

app.get('/:id', function (req, res) {
    console.log(temperatura[req.params.id]);
    res.send(temperatura[req.params.id]);
})

app.put('/:id', function (req, res) {
    temperatura[req.params.id] = req.body;
    res.send(temperatura[req.params.id]);
})

app.listen(3333);