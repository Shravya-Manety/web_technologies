/* eslint-disable no-undef */
import React from 'react'
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import FavoriteCardComponent from './FavoriteCardComponent'
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Zoom } from 'react-toastify';


const localStorage = window.localStorage
class FavoritesComponent extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            articleList: [],
        }
    }
    componentDidMount() {
        let localStorage = window.localStorage;
        this.setState({
            articleList: JSON.parse(localStorage.getItem('favoriteArticles'))
        })
    }
    handleDeleteFromFavorites = (event, newsItemId, newsItemTitle) => {
        event.stopPropagation()
        let articleList = JSON.parse(localStorage.getItem('favoriteArticles'));
        const articleKey = newsItemId.toString()
        // remove from array
        const index = articleList.indexOf(articleKey);
        if (index !== -1) articleList.splice(index, 1);
        localStorage.setItem('favoriteArticles', JSON.stringify(articleList))
        // remove from localstorage
        localStorage.removeItem(articleKey)
        this.setState({
            articleList: JSON.parse(localStorage.getItem('favoriteArticles'))
        })
        toast("Removing - " + newsItemTitle,{
            className: 'font-black'});
    }
    render() {
        if (this.state.articleList !== undefined && this.state.articleList !== null && this.state.articleList.length > 0) {
            console.log("in favorites component:", this.state.articleList)
            var listOfNews = this.state.articleList.map((newsItemId) => {
                const newsItem = JSON.parse(localStorage.getItem(newsItemId))
                console.log("newsItem in favorites", newsItem)
                return <Col md={3} key={newsItem.id} id={newsItem.id}><FavoriteCardComponent newsObj = {newsItem} handleDeleteFromFavorites={this.handleDeleteFromFavorites} /></Col>
            })
        }
        return (
            <>
                <ToastContainer position={toast.POSITION.TOP_CENTER} transition={Zoom} autoclose={3000} hideProgressBar={true} className = {"toast-message"} closeOnClick = {false}/>
                {
                    this.state.articleList === undefined || this.state.articleList === null || this.state.articleList.length === 0
                        ?
                        <div className="no-favorites">
                            <p>You have no saved articles</p>
                        </div>
                        :
                        <Container fluid>
                            <Row>
                                <Col className="search-results">Favorites</Col>
                            </Row>
                            <Row>
                                {listOfNews}
                            </Row>
                        </Container>
                }
            </>
        )
    }
}

export default FavoritesComponent