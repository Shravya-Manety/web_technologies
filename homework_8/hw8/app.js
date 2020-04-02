const express = require('express')
const app = express()
const port = 3000
const guardianApiKey = 'b2c3d621-44d1-4279-836b-25a5204faaa9'
const nytApiKey = 'djz3Uk7tCWCGUjhsgXlrWF67Z6YGYFWw'

const axios = require('axios')

// ------------------------------------ GUARDIAN NEWS API -----------------------------------

function getNewsFromAPICall(urlObj, processResponse, type){
    return new Promise( function(resolve, reject){
        axios(urlObj)
        .then(response => {
            const newsResponse = processResponse(response.data, type)
            resolve(newsResponse)
        })
        .catch(function(err){
            reject(err);
        });
    })
}
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

app.get('/guardianHome', async(request, response) => {
    try{
        let uriHome = 'https://content.guardianapis.com/search?api-key='+guardianApiKey+'&section=(world|sport|business|technology|politics)&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriHome, processResGuardian, 'home');
        response.send(newsResult);
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardianWorld', async(request, response) =>{
    try{
        let uriWorld = 'https://content.guardianapis.com/world?api-key='+guardianApiKey+'&show-blocks=all&page-size=15';
        const newsResult = await getNewsFromAPICall(uriWorld, processResGuardian, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardianPolitics', async(request, response) =>{
    try{
        let uriPolitics = 'https://content.guardianapis.com/politics?api-key='+guardianApiKey+'&show-blocks=all&page-size=15';
        const newsResult = await getNewsFromAPICall(uriPolitics, processResGuardian, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});

app.get('/guardianBusiness', async(request, response)=>{
    try{
        let uriBusiness = 'https://content.guardianapis.com/business?api-key='+guardianApiKey+'&show-blocks=all&page-size=15'
        const newsResult = await getNewsFromAPICall(uriBusiness, processResGuardian, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});

app.get('/guardianTech', async(request, response) =>{
    try{
        let uriTechnology = 'https://content.guardianapis.com/technology?api-key='+guardianApiKey+'&show-blocks=all&page-size=15'
        const newsResult = await getNewsFromAPICall(uriTechnology, processResGuardian, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});

app.get('/guardianSports', async(request, response) =>{
    try{
        let uriSports = 'https://content.guardianapis.com/sport?api-key='+guardianApiKey+'&show-blocks=all&page-size=15'
        const newsResult = await getNewsFromAPICall(uriSports, processResGuardian, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardianDetail/*', async(request, response) => {
    try{
        let uriDetail = 'https://content.guardianapis.com/'+request.params[0]+'?api-key='+guardianApiKey+'&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriDetail, processResGuardian, 'detail');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardianSearch/:queryKeyword', async(request, response)=>{
    try{
        let uriSearch = 'https://content.guardianapis.com/search?q='+request.params["queryKeyword"]+'&api-key='+guardianApiKey+'&show-blocks=all'
        const newsResult = await getNewsFromAPICall(uriSearch, processResGuardian, 'search');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
});

function processResGuardian(jsonObj, type){
    let finalResult = [];
    let results = []
    if(type !== 'detail'){
        results = jsonObj.response.results;
    }
    else{
        results.push(jsonObj.response.content);
    }
    let requiredKeys = ['webTitle', 'sectionId', 'webPublicationDate', 'webUrl']
    if(type !== 'detail') requiredKeys.push('id');
    for (let news of results){
        let addObj = {}
        countKeys = 0;
        requiredKeys.forEach((key) => {
            if(key in news && news[key] !== null && news[key] !== ""){
                countKeys++;
            }
        });
        
        if(type !== 'search'){
            let desc = news.blocks.body[0].bodyTextSummary;
            if(desc && desc !== null && desc != ''){
                countKeys++;
            }
        }
        if((type === 'search' && countKeys === 5) || (type === 'detail' && countKeys === 5) || countKeys === 6){
            addObj['id'] = news['id']; //id
            addObj['title'] = news['webTitle']; //title
            sectionName = news['sectionId'] === 'sport' ? 'sports' : news['sectionId']
            addObj['section'] = sectionName; //section
            addObj['date'] = news['webPublicationDate']; //published date
            addObj['link'] = news['webUrl']; //url for sharing
            if(type !== 'search'){
                addObj['description'] = news.blocks.body[0].bodyTextSummary; //description
            }
            if(news.blocks && news.blocks.main && news.blocks.main.elements && news.blocks.main.elements.length > 0 && news.blocks.main.elements[0].assets )
            {
                imgAsset = news.blocks.main.elements[0].assets;
                if(imgAsset && imgAsset != null && imgAsset.length !== 0 && imgAsset[imgAsset.length-1].file !== undefined && imgAsset[imgAsset.length-1].file !== null){
                    addObj['image'] = imgAsset[imgAsset.length-1].file; //image
                }
            }
            if(!addObj.hasOwnProperty("image"))
            {
                addObj['image'] = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png'; //default image
            }
            finalResult.push(addObj);
            if(type === 'search' && finalResult.length === 5) break;
            else if(type === 'section' && finalResult.length === 10) break;
        }
    }
    return finalResult;
}

// ----------------------------------------------- NYT ------------------------------------------------------

app.get('/nytHome', async(request,response)=>{
    try{
        let uriHome = 'https://api.nytimes.com/svc/topstories/v2/home.json?api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriHome, processResNYT, 'home');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nytWorld', async(request, response) =>{
    try{
        let uriWorld = 'https://api.nytimes.com/svc/topstories/v2/world.json?api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriWorld, processResNYT, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nytPolitics', async(request, response)=>{
    try{
        let uriPolitics = 'https://api.nytimes.com/svc/topstories/v2/politics.json?api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriPolitics, processResNYT, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nytBusiness', async(request, response) => {
    try{
        let uriBusiness = 'https://api.nytimes.com/svc/topstories/v2/business.json?api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriBusiness, processResNYT, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
    
})
app.get('/nytTech', async(request, response) => {
    try{
        let uriTech = 'https://api.nytimes.com/svc/topstories/v2/technology.json?api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriTech, processResNYT, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nytSports', async(request, response) => {
    try{
        let uriSports = 'https://api.nytimes.com/svc/topstories/v2/sports.json?api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriSports, processResNYT, 'section');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nytDetail/*', async(request, response) =>{
    try{
        let uriDetail = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?fq=web_url:("'+request.params[0]+'") &api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriDetail, processResNYTDetail, 'detail');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nytSearch/:queryKeyword', async(request, response) => {
    try{
        let uriSearch = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?q='+request.params["queryKeyword"]+'&api-key='+nytApiKey
        const newsResult = await getNewsFromAPICall(uriSearch, processResNYTSearch, 'search');
        response.send(newsResult)
    }
    catch(error){
        response.send(error)
    }
})
function processResNYT(jsonObj){
    let finalResult = [];
    let results = jsonObj.results;
    requiredKeys = ['url', 'title', 'section', 'published_date', 'abstract', 'multimedia']
    for (let news of results){
        let addObj = {}
        let countKeys = 0;
        requiredKeys.forEach((key) => {
            if(key in news && news[key] !== null && news[key] !== ""){
                countKeys++;
            }
        })
        if(countKeys === 6){
            addObj['title'] = news['title']; //title
            addObj['section'] = news['section']; //section
            addObj['date'] = news['published_date']; // date published
            addObj['description'] = news['abstract']; //description
            addObj['link'] = news['url'] //link for sharing
            if(news.multimedia && news.multimedia !== null && news.multimedia.length > 0){
                for(let media of news.multimedia){
                    if(media.width >= 2000){
                        addObj['image'] = media.url;
                        break;
                    }
                }
            } 
            if(!addObj.hasOwnProperty("image")){
                addObj['image'] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
            }
            finalResult.push(addObj);
            if(finalResult.length === 10) break;
        }
    }
    return finalResult;
}
function processResNYTDetail(jsonObj){
    let addObj = {};
    const imageBaseURL = 'https://www.nytimes.com/';
    let finalResult = [];
    let news = jsonObj.response.docs[0];
    requiredKeys = ['headline', 'multimedia', 'pub_date', 'web_url', 'abstract', 'news_desk']
    let countKeys = 0;
    requiredKeys.forEach((key) => {
        if(key === 'headline' && (news.headline.main !== null || news.headline.main !== '')) countKeys ++;
        else if(key in news && news[key] !== null && news[key] !== "") countKeys ++;
    })
    if(countKeys === 6){
        addObj['id'] = news['web_url']
        addObj['title'] = news['headline']['main']; //title
        addObj['date'] = news['pub_date']; // date published
        addObj['link'] = news['web_url'] //link for sharing
        addObj['section'] = news['news_desk']//section for favorites
        addObj['description'] = news['abstract']
        for(let media of news.multimedia){
            if(media.width >= 2000){
                addObj['image'] = imageBaseURL + media['url'];
                break;
            }
        }
        if(!addObj.hasOwnProperty("image")){
            addObj['image'] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
        }
        finalResult.push(addObj);
    }
    return finalResult;
}
function processResNYTSearch(jsonObj){
    const imageBaseURL = 'https://www.nytimes.com/'
    let finalResult = [];
    let results = jsonObj.response.docs;
    requiredKeys = ['headline', 'multimedia', 'pub_date', 'web_url', 'news_desk']
    for (let news of results){
        let addObj = {}
        let countKeys = 0;
        requiredKeys.forEach((key) => {
            if(key === 'headline' && (news.headline.main !== null || news.headline.main !== '')) countKeys ++;
            else if(key in news && news[key] !== null && news[key] !== "") countKeys ++;   
        })
        if(countKeys === 5){
            addObj['title'] = news['headline']['main']; //title
            addObj['date'] = news['pub_date']; // date published
            addObj['link'] = news['web_url'] //link for sharing
            addObj['section'] = news['news_desk']
            for(let media of news.multimedia){
                if(media.width >= 2000){
                    addObj['image'] = imageBaseURL + media['url'];
                    break;
                }
            }
            if(!addObj.hasOwnProperty("image")){
                addObj['image'] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
            }
            finalResult.push(addObj);
            if(finalResult.length === 5) break;
        }
    }
    return finalResult;
}





app.get('/', (req, res)=> res.send('Hello World!'))

app.listen(port, ()=> console.log(`Express App listening on port ${port}!`))

