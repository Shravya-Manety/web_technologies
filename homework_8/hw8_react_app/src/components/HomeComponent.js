import React from 'react'
import NewsCardComponent from './NewsCardComponent'
import PropTypes from 'prop-types'

import { formatDate } from './Utilties'
import LoadingComponent from './LoadingComponent'

const axios = require('axios')
class HomeComponent extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            news: [],
        }
    }
    static getDerivedStateFromProps(nextProps){
        return({
            isGuardian : nextProps.isGuardian
        })
    }
    componentDidUpdate(prevProps){
        if(this.props.isGuardian !== prevProps.isGuardian){
            this.makeAPICall()
            this.setState({
                news : [],
            })
        }
    }
    componentDidMount() {
        this.makeAPICall()   
    }
    makeAPICall() {
        const baseUrl = "http://127.0.0.1:3000/"
        const newsType = this.props.isGuardian ? "guardian" : "nyt"
        axios(baseUrl + newsType + "_home")
            .then(response => {
                this.setState({
                    news: response.data
                })
            })
    }

    render() {
        
        const listOfNewsCards = this.state.news.map((newsItem) => {
            const date = formatDate(newsItem.date);
            const keyValue = this.props.isGuardian ? newsItem.id : newsItem.link 
            return <NewsCardComponent key={keyValue} isGuardian={this.props.isGuardian} id={keyValue} imgUrl={newsItem.image} title={newsItem.title} date={date} section={newsItem.section} description={newsItem.description} link = {newsItem.link}/>
        })
        return (
            <>
            {
                this.state.news.length === 0
                ?
                <LoadingComponent />
                :
                [listOfNewsCards]}
            </>
        )
    }
}
HomeComponent.propTypes={
    isGuardian : PropTypes.bool.isRequired,
}

export default HomeComponent