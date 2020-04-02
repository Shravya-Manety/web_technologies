const express = require('express')
const app = express()
const port = 3000
const guardianApikey = 'b2c3d621-44d1-4279-836b-25a5204faaa9'
const nyt_apikey = 'djz3Uk7tCWCGUjhsgXlrWF67Z6YGYFWw'
const clonedeep = require('lodash.clonedeep')
let news_resp;

const axios = require('axios')

// ------------------------------------ GUARDIAN NEWS API -----------------------------------

function getNewsFromAPICall(urlObj, news, type){
    return new Promise( function(resolve, reject){
        axios(urlObj)
        .then(response => {
            let news_resp;
            if(news === 'nyt'){
                if(type === 'detail') news_resp = processResNYTDetail(response.data);
                else if(type === 'search') news_resp = processResNYTSearch(response.data)
                else news_resp = processResNYT(response.data);
            }
            else news_resp = processResGuardian(response.data, type);
            resolve(news_resp)
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

app.get('/guardian_home', async(request, response) => {
    try{
        let uriHome = 'https://content.guardianapis.com/search?api-key='+guardianApikey+'&section=(world|sport|business|technology|politics)&show-blocks=all'
        const news_result = await getNewsFromAPICall(uriHome, 'guardian', 'home');
        response.send(news_result);
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardian_world', async(request, response) =>{
    try{
        let uriWorld = 'https://content.guardianapis.com/world?api-key='+guardianApikey+'&show-blocks=all&page-size=15';
        const news_result = await getNewsFromAPICall(uriWorld, 'guardian',  'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardian_politics', async(request, response) =>{
    try{
        let uriPolitics = 'https://content.guardianapis.com/politics?api-key='+guardianApikey+'&show-blocks=all&page-size=15';
        const news_result = await getNewsFromAPICall(uriPolitics, 'guardian', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});

app.get('/guardian_business', async(request, response)=>{
    try{
        let uriBusiness = 'https://content.guardianapis.com/business?api-key='+guardianApikey+'&show-blocks=all&page-size=15'
        const news_result = await getNewsFromAPICall(uriBusiness, 'guardian', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});

app.get('/guardian_tech', async(request, response) =>{
    try{
        let uriTechnology = 'https://content.guardianapis.com/technology?api-key='+guardianApikey+'&show-blocks=all&page-size=15'
        const news_result = await getNewsFromAPICall(uriTechnology, 'guardian', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});

app.get('/guardian_sports', async(request, response) =>{
    try{
        let uriSports = 'https://content.guardianapis.com/sport?api-key='+guardianApikey+'&show-blocks=all&page-size=15'
        const news_result = await getNewsFromAPICall(uriSports, 'guardian', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardian_detail/*', async(request, response) => {
    try{
        let uriDetail = 'https://content.guardianapis.com/'+request.params[0]+'?api-key='+guardianApikey+'&show-blocks=all'
        const news_result = await getNewsFromAPICall(uriDetail, 'guardian', 'detail');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});
app.get('/guardian_search/:query_keyword', async(request, response)=>{
    try{
        let uriSearch = 'https://content.guardianapis.com/search?q='+request.params["query_keyword"]+'&api-key='+guardianApikey+'&show-blocks=all'
        const news_result = await getNewsFromAPICall(uriSearch, 'guardian', 'search');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
});

function processResGuardian(jsonObj, type){
    let obj = {
        'id' : '',
        'title' : '',
        'image' : '',
        'section' : '',
        'date' : '',
        'description' : '',
        'link' : ''
    };
    let final_result = [];
    let results = []
    if(type !== 'detail'){
        results = jsonObj.response.results;
    }
    else{
        results.push(jsonObj.response.content);
    }
    let required_keys = ['webTitle', 'sectionId', 'webPublicationDate', 'webUrl']
    if(type !== 'detail') required_keys.push('id');
    for (let news of results){
        let add_obj = clonedeep(obj)
        count_keys = 0;
        required_keys.forEach((key) => {
            if(key in news && news[key] !== null && news[key] !== ""){
                count_keys++;
            }
        });
        
        if(type !== 'search'){
            let desc = news.blocks.body[0].bodyTextSummary;
            if(desc && desc !== null && desc != ''){
                count_keys++;
            }
        }
        if((type === 'search' && count_keys === 5) || (type === 'detail' && count_keys === 5) || count_keys === 6){
            
            add_obj['id'] = news['id']; //id
            add_obj['title'] = news['webTitle']; //title
            sectionName = news['sectionId'] === 'sport' ? 'sports' : news['sectionId']
            add_obj['section'] = sectionName; //section
            add_obj['date'] = news['webPublicationDate']; //published date
            add_obj['link'] = news['webUrl']; //url for sharing
            if(type !== 'search'){
                add_obj['description'] = news.blocks.body[0].bodyTextSummary; //description
            }
            if(news.blocks && news.blocks.main && news.blocks.main.elements && news.blocks.main.elements.length > 0 && news.blocks.main.elements[0].assets )
            {
                img_asset = news.blocks.main.elements[0].assets;
                if(img_asset && img_asset != null && img_asset.length !== 0 && img_asset[img_asset.length-1].file !== undefined && img_asset[img_asset.length-1].file !== null){
                    add_obj['image'] = img_asset[img_asset.length-1].file; //image
                }
            }
            if(add_obj['image'] === '')
            {
                add_obj['image'] = 'https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png'; //default image
            }
            final_result.push(add_obj);
            if(type === 'search' && final_result.length === 5) break;
            else if(type === 'section' && final_result.length === 10) break;
        }
    }
    // console.log("final_result : ", final_result)
    return final_result;
}

// ----------------------------------------------- NYT ------------------------------------------------------

app.get('/nyt_home', async(request,response)=>{
    try{
        let uriHome = 'https://api.nytimes.com/svc/topstories/v2/home.json?api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriHome, 'nyt', 'home');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nyt_world', async(request, response) =>{
    try{
        let uriWorld = 'https://api.nytimes.com/svc/topstories/v2/world.json?api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriWorld, 'nyt', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nyt_politics', async(request, response)=>{
    try{
        let uriPolitics = 'https://api.nytimes.com/svc/topstories/v2/politics.json?api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriPolitics, 'nyt', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nyt_business', async(request, response) => {
    try{
        let uriBusiness = 'https://api.nytimes.com/svc/topstories/v2/business.json?api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriBusiness, 'nyt', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
    
})
app.get('/nyt_tech', async(request, response) => {
    try{
        let uriTech = 'https://api.nytimes.com/svc/topstories/v2/technology.json?api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriTech, 'nyt', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nyt_sports', async(request, response) => {
    try{
        let uriSports = 'https://api.nytimes.com/svc/topstories/v2/sports.json?api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriSports, 'nyt', 'section');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nyt_detail/*', async(request, response) =>{
    try{
        let uriDetail = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?fq=web_url:("'+request.params[0]+'") &api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriDetail, 'nyt', 'detail');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
app.get('/nyt_search/:query_keyword', async(request, response) => {
    try{
        let uriSearch = 'https://api.nytimes.com/svc/search/v2/articlesearch.json?q='+request.params["query_keyword"]+'&api-key='+nyt_apikey
        const news_result = await getNewsFromAPICall(uriSearch, 'nyt', 'search');
        response.send(news_result)
    }
    catch(error){
        response.send(error)
    }
})
function processResNYT(jsonObj){
    let obj = {
        'title' : '',
        'image' : '',
        'section' : '',
        'date' : '',
        'description' : '',
        'link' : ''
    };
    let final_result = [];
    let results = jsonObj.results;
    required_keys = ['url', 'title', 'section', 'published_date', 'abstract', 'multimedia']
    for (let news of results){
        let add_obj = clonedeep(obj)
        let count_keys = 0;
        required_keys.forEach((key) => {
            if(key in news && news[key] !== null && news[key] !== ""){
                count_keys++;
            }
        })
        if(count_keys === 6){
            add_obj['title'] = news['title']; //title
            add_obj['section'] = news['section']; //section
            add_obj['date'] = news['published_date']; // date published
            add_obj['description'] = news['abstract']; //description
            add_obj['link'] = news['url'] //link for sharing
            if(news.multimedia && news.multimedia !== null && news.multimedia.length > 0){
                for(let media of news.multimedia){
                    if(media.width >= 2000){
                        add_obj['image'] = media.url;
                        break;
                    }
                }
            }
            if(add_obj['image'] === '') add_obj['image'] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
            final_result.push(add_obj);
            if(final_result.length === 10) break;
        }
    }
    return final_result;
}
function processResNYTDetail(jsonObj){
    let add_obj = {
        'title' : '',
        'image' : '',
        'date' : '',
        'description' : '',
        'link' : '',
        'section' : ''
        
    };
    const imageBaseURL = 'https://www.nytimes.com/';
    let final_result = [];
    let news = jsonObj.response.docs[0];
    required_keys = ['headline', 'multimedia', 'pub_date', 'web_url', 'abstract', 'news_desk']
    let count_keys = 0;
    required_keys.forEach((key) => {
        if(key === 'headline' && (news.headline.main !== null || news.headline.main !== '')) count_keys ++;
        else if(key in news && news[key] !== null && news[key] !== "") count_keys ++;
    })
    if(count_keys === 6){
        add_obj['id'] = news['web_url']
        add_obj['title'] = news['headline']['main']; //title
        add_obj['date'] = news['pub_date']; // date published
        add_obj['link'] = news['web_url'] //link for sharing
        add_obj['section'] = news['news_desk']//section for favorites
        add_obj['description'] = news['abstract']
        for(let media of news.multimedia){
            if(media.width >= 2000){
                add_obj['image'] = imageBaseURL + media['url'];
                break;
            }
        }
        if(add_obj['image'] === '') add_obj['image'] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
        final_result.push(add_obj);
    }
    return final_result;
}
function processResNYTSearch(jsonObj){
    let obj = {
        'title' : '',
        'image' : '',
        'section' : '',
        'date' : '',
        'link' : ''
    };
    const imageBaseURL = 'https://www.nytimes.com/'
    let final_result = [];
    let results = jsonObj.response.docs;
    required_keys = ['headline', 'multimedia', 'pub_date', 'web_url', 'news_desk']
    for (let news of results){
        let add_obj = clonedeep(obj)
        let count_keys = 0;
        required_keys.forEach((key) => {
            if(key === 'headline' && (news.headline.main !== null || news.headline.main !== '')) count_keys ++;
            else if(key in news && news[key] !== null && news[key] !== "") count_keys ++;   
        })
        if(count_keys === 5){
            add_obj['title'] = news['headline']['main']; //title
            add_obj['date'] = news['pub_date']; // date published
            add_obj['link'] = news['web_url'] //link for sharing
            add_obj['section'] = news['news_desk']
            for(let media of news.multimedia){
                if(media.width >= 2000){
                    add_obj['image'] = imageBaseURL + media['url'];
                    break;
                }
            }
            if(add_obj['image'] === '') add_obj['image'] = 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Nytimes_hq.jpg';
            final_result.push(add_obj);
            if(final_result.length === 5) break;
        }
    }
    return final_result;
}





app.get('/', (req, res)=> res.send('Hello World!'))

app.listen(port, ()=> console.log(`Express App listening on port ${port}!`))

