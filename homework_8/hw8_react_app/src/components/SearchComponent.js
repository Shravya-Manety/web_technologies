import React from 'react'
import { Container, Row, Col } from 'react-bootstrap'
import PropTypes from 'prop-types'
import SearchCardComponent from './SearchCardComponent'
import { formatDate } from './Utilties'
import LoadingComponent from './LoadingComponent'

const axios = require('axios')
const baseUrl = "http://127.0.0.1:3000/"
class SearchComponent extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            searchValue: this.props.searchValue,
            guardianRes:{},
            nytRes:{},
            searchReq: true
        }
    }
    componentWillReceiveProps(nextProps) {
        if(this.props.searchValue !== nextProps.searchValue){
            this.setState({
                searchValue: nextProps.searchValue,
                searchReq: true,
            })
        }
    }
    // static getDerivedStateFromProps(props, nextProps){
    //     if (props.searchValue !== nextProps.searchValue){
    //         return({
    //             searchValue: nextProps.searchValue,
    //             searchReq: true,
    //         })
    //     }
    //     return null
    // }
    componentDidUpdate() {
        console.log("componentDidUpdate: ",this.state.searchReq)
        if (this.state.searchReq) {
            this.makeGuardianAPICall()
            this.makeNytAPICall()
            this.setState({
                searchReq: false,
            })
        }
    }
    componentDidMount() {
        this.makeGuardianAPICall()
        this.makeNytAPICall()
        this.setState({
            searchReq: false,
        })
    }
    makeNytAPICall() {
        axios(baseUrl + "nyt_search/" + this.state.searchValue)
            .then(response => {
                console.log("nytimes",response.data)
                this.setState({
                    nytRes: response.data
                })
            })
    }
    makeGuardianAPICall() {
        axios(baseUrl + "guardian_search/" + this.state.searchValue)
            .then(response => {
                console.log("guardian",response.data)
                this.setState({
                    guardianRes: response.data
                })
            })
    }

    render() {
        console.log("searchReq :", this.state.searchReq)
        console.log("render:", this.state.guardianRes)
        if (this.state.guardianRes instanceof Array && this.state.guardianRes.length > 0){
            var listOfNews = this.state.guardianRes.map((newsItem) => {
                const date = formatDate(newsItem.date);
                return <Col md={3} key={newsItem.id}><SearchCardComponent id={newsItem.id} isGuardian={true} title={newsItem.title + " "} date={date} section={newsItem.section} imgUrl={newsItem.image} link={newsItem.link} /></Col>
            })
        }
        else{
            listOfNews = [];
        }
        console.log("listOfNews:", listOfNews)
        console.log("render:", this.state.guardianRes)
        if(this.state.nytRes instanceof Array && this.state.nytRes.length > 0){

            var tempListOfNews = Array.isArray(this.state.nytRes) && this.state.nytRes.map((newsItem) => {
                const date = formatDate(newsItem.date);
                return <Col md={3} key={newsItem.link}><SearchCardComponent id={newsItem.link} isGuardian={false} title={newsItem.title + " "} date={date} section={newsItem.section} imgUrl={newsItem.image} link={newsItem.link} /></Col>
            })
            console.log("tempListOfNews :", tempListOfNews)
        }
        if (listOfNews !== undefined && listOfNews !== null && tempListOfNews !== undefined && tempListOfNews !== null && tempListOfNews.length === 5) {
            listOfNews.push.apply(listOfNews, tempListOfNews)
        }
        const guardianResState  = this.state.guardianRes;
        const nytResState = this.state.nytRes
        console.log("inside search component render")
        return (
            <>
                {/* {   (this.state.guardianRes === null || this.state.guardianRes.length === 0) && (this.state.nytRes === null || this.state.nytRes.length ===0) */
                    
                    guardianResState instanceof Array && nytResState instanceof Array
                    ?
                    <Container fluid>
                        {
                            // this.state.searchReq === false && (listOfNews === null || listOfNews.length === 0)
                            (guardianResState.length === 0) && (nytResState.length ===0)
                                ?
                                <Col className="no-results">No Results</Col>
                                :
                                <>
                                <Row>
                                    <Col className="search-results">Results</Col>
                                </Row>
                                <Row>
                                    {listOfNews}
                                </Row>
                                </>
                        }

                    </Container>
                    :
                    <LoadingComponent />
                }
            </>
        )
    }
}
SearchComponent.propTypes = {
    searchValue : PropTypes.string.isRequired,
}
export default SearchComponent