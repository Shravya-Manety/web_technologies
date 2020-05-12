const express = require('express')
const app = express()
const port = 8080
const guardianApiKey = 'b2c3d621-44d1-4279-836b-25a5204faaa9'
var ip = require('ip');

const axios = require('axios')
const googleTrends = require('google-trends-api');

// ------------------------------------ GUARDIAN NEWS API -----------------------------------

function getNewsFromAPICall(urlObj, processResponse) {
    return new Promise(function (resolve, reject) {
        axios(urlObj)
            .then(response => {
                const newsResponse = processResponse(response.data)
                resolve(newsResponse)
            })
            .catch(function (err) {
                reject(err);
            });
    })
}
app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

app.get('/guardianHome', async (request, response) => {
    try {
        let uriHome = 'https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=' + guardianApiKey;
        const newsResult = await getNewsFromAPICall(uriHome, processResGuardianHome);
        response.send(newsResult);
    }
    catch (error) {
        response.send(error)
    }
});
app.get('/guardianWorld', async (request, response) => {
    try {
        let uriWorld = 'https://content.guardianapis.com/world?api-key=' + guardianApiKey + '&show-blocks=all';
        const newsResult = await getNewsFromAPICall(uriWorld, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});
app.get('/guardianPolitics', async (request, response) => {
    try {
        let uriPolitics = 'https://content.guardianapis.com/politics?api-key=' + guardianApiKey + '&show-blocks=all';
        const newsResult = await getNewsFromAPICall(uriPolitics, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});

app.get('/guardianBusiness', async (request, response) => {
    try {
        let uriBusiness = 'https://content.guardianapis.com/business?api-key=' + guardianApiKey + '&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriBusiness, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});

app.get('/guardianTech', async (request, response) => {
    try {
        let uriTechnology = 'https://content.guardianapis.com/technology?api-key=' + guardianApiKey + '&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriTechnology, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});

app.get('/guardianSports', async (request, response) => {
    try {
        let uriSports = 'https://content.guardianapis.com/sport?api-key=' + guardianApiKey + '&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriSports, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});
app.get('/guardianScience', async (request, response) => {
    try {
        let uriSports = 'http://content.guardianapis.com/science?api-key=' + guardianApiKey + '&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriSports, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});

app.get('/guardianDetail/*', async (request, response) => {
    try {
        let uriDetail = 'https://content.guardianapis.com/' + request.params[0] + '?api-key=' + guardianApiKey + '&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriDetail, processResGuardianDetail);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});
app.get('/guardianSearch/:queryKeyword', async (request, response) => {
    try {
        let uriSearch = 'https://content.guardianapis.com/search?q=' + request.params["queryKeyword"] + '&api-key=' + guardianApiKey + '&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriSearch, processResGuardianSectionSearch);
        response.send(newsResult)
    }
    catch (error) {
        response.send(error)
    }
});
app.get('/getGoogleTrendsData/:queryKeyword', (request, response) => {
    googleTrends.interestOverTime({ keyword: request.params["queryKeyword"], startTime: new Date('2019-06-01') })
        .then(function (results) {
            let parsed = JSON.parse(results)
            let finalResults = [];
            let valuesArray = parsed.default.timelineData
            if (valuesArray !== undefined && valuesArray !== null && valuesArray.length > 0) {
                for (const value of valuesArray) {
                    finalResults.push(value.value[0])
                }
            }
            response.send(finalResults);
        })
        .catch(function (err) {
            response.send(err)
        });
});
function processResGuardianHome(jsonObj) {
    let finalResult = [];
    let results = jsonObj.response.results;
    let requiredKeys = ['webTitle', 'sectionName', 'webPublicationDate', 'webUrl', 'id']
    for (let news of results) {
        let addObj = {}
        countKeys = 0;
        requiredKeys.forEach((key) => {
            if (key in news && news[key] !== null && news[key] !== "") {
                countKeys++;
            }
        });
        if (countKeys === 5) {
            addObj['id'] = news['id']; //id
            addObj['title'] = news['webTitle']; //title
            addObj['section'] = news['sectionName']; //section
            addObj['date'] = news['webPublicationDate']; //published date
            addObj['link'] = news['webUrl']; //url for sharing
            addObj['image'] = processImage(news, 'home')
            finalResult.push(addObj);
        }
    }

    return finalResult;
}

function processResGuardianSectionSearch(jsonObj) {
    let finalResult = [];
    let results = jsonObj.response.results;
    let requiredKeys = ['webTitle', 'sectionId', 'webPublicationDate', 'webUrl', 'id']
    for (let news of results) {
        let addObj = {}
        countKeys = 0;
        requiredKeys.forEach((key) => {
            if (key in news && news[key] !== null && news[key] !== "") {
                countKeys++;
            }
        });
        if (countKeys === 5) {
            addObj['id'] = news['id']; //id
            addObj['title'] = news['webTitle']; //title
            addObj['section'] = news['sectionName']; //section
            addObj['date'] = news['webPublicationDate']; //published date
            addObj['link'] = news['webUrl']; //url for sharing
            addObj['image'] = processImage(news, 'sectionSearch') //image
            finalResult.push(addObj);
        }
    }
    // finalResult.sort((a, b) => (a.date.localeCompare(b.date) * -1))
    return finalResult;
}
function processResGuardianDetail(jsonObj) {
    let finalResult = [];
    let news = jsonObj.response.content
    let requiredKeys = ['webTitle', 'sectionName', 'webPublicationDate', 'webUrl', 'id']
    let addObj = {}
    let descList = []
    countKeys = 0;
    requiredKeys.forEach((key) => {
        if (key in news && news[key] !== null && news[key] !== "") {
            countKeys++;
        }
    });
    if (countKeys === 5) {
        addObj['id'] = news['id']; //id
        addObj['title'] = news['webTitle']; //title
        addObj['section'] = news['sectionName']; //section
        addObj['date'] = news['webPublicationDate']; //published date
        addObj['link'] = news['webUrl']; //url for sharing
        let desc = news.blocks.body;
        if (desc && desc.length > 0) {
            for (const descObj of desc) {
                if (descObj != null && descObj.bodyHtml != undefined && descObj.bodyHtml != null && descObj.bodyHtml != '') {
                    descList.push(descObj.bodyHtml);
                }
            }
            addObj['description'] = descList.join(' '); //description
        }
        if(!addObj.hasOwnProperty('description')){
            addObj['description'] = "";
        }
        addObj['image'] = processImage(news, 'detail') //image
        finalResult.push(addObj);
    }
    return finalResult;
}
function processImage(news, type) {
    let imageUrl = null;
    if (type === 'home') {
        imageUrl = processImageHome(news)
    }
    else {
        imageUrl = processImageGeneral(news)
    }
    if (imageUrl !== null && imageUrl !== '') {
        return imageUrl;
    }
    return 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png';
}
function processImageHome(news) {
    if (news.fields && news.fields.thumbnail && news.fields.thumbnail != undefined && news.fields.thumbnail !== null) {
        return news.fields.thumbnail; //image
    }
    return null;
}
function processImageGeneral(news) {
    if (news.blocks && news.blocks.main && news.blocks.main.elements && news.blocks.main.elements.length > 0 && news.blocks.main.elements[0].assets) {
        imgAsset = news.blocks.main.elements[0].assets;
        if (imgAsset && imgAsset != null && imgAsset.length !== 0 && imgAsset[0].file !== undefined && imgAsset[0].file !== null) {
            return imgAsset[0].file; //image
        }
    }
    return null
}
app.listen(port, () => console.log(`Express App listening on port ${port}!`))