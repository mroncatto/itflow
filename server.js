const express = require('express');
const path = require('path');
const app = express();
const cors = require('cors');

app.use(express.static(__dirname + '/dist/itflow'));
app.get('/*', function (req, res) {
    res.sendFile(path.join(__dirname +
        '/dist/itflow/index.html'));
});

const corsOptions = {
    origin: '*',
    optionsSuccessStatus: 200 
  }
app.use(cors(corsOptions));
app.listen(process.env.PORT || 8080);