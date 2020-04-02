/* eslint-disable no-undef */
import React, { Fragment } from 'react'
import Collapse from 'react-bootstrap/Collapse'
import { IconContext } from "react-icons";
import { FaChevronDown, FaChevronUp, FaRegBookmark, FaBookmark } from 'react-icons/fa'
import ReactTooltip from 'react-tooltip'
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import _ from 'lodash';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Zoom } from 'react-toastify';
import Media from 'react-media'
import PropTypes from 'prop-types'
import { format } from 'date-fns'

import { formatDescription} from './Utilties'
import CommentBoxComponent from './CommentBoxComponent'
import LoadingComponent from './LoadingComponent'
import FbShareComponent from './FbShareComponent';
import TwitterShareComponent from './TwitterShareComponent'
import EmailShareComponent from './EmailShareComponent'

const axios = require('axios')
const localStorage = window.localStorage;


class DetailedComponent extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            isGuardian: this.props.history.location.state.isGuardian,
            detailsUrl: this.props.location.search.substring(4),
            extended: false,
            result: [],
            bookmarked: false,
        }
        this.advDescRef = React.createRef();
        this.addToFavorites = this.addToFavorites.bind(this)
        this.removeFromFavorites = this.removeFromFavorites.bind(this)
    }
    
    componentDidMount() {
        const baseUrl = "http://localhost:3000/"
        const detailType = this.state.isGuardian ? 'guardian_detail/' : 'nyt_detail/'
        axios((baseUrl + detailType + this.state.detailsUrl))
            .then((response) => {
                console.log(response.data[0])
                this.setState({
                    result: response.data[0]
                })
            })
    }
    componentDidUpdate() {
        ReactTooltip.rebuild();
    }
    handleClick(type) {
        this.setState((prevState) => {
            return {
                extended: !prevState.extended,
            }
        })
        if (type === "down") {
            this.advDescRef.current.scrollIntoView({ behavior: 'smooth' })
        }
        else if (type === "up") {
            this.props.handleUpArrowClick();
        }
    }
    addToFavorites() {
        ReactTooltip.hide();
        let articleList = JSON.parse(localStorage.getItem('favoriteArticles'));
        const articleKey = this.state.result.id.toString()
        // add to localstorage
        let newsObj = _.cloneDeep(this.state.result)
        newsObj['isGuardian'] = this.state.isGuardian
        newsObj['section'] = this.state.result.section
        delete newsObj.description
        localStorage.setItem(articleKey, JSON.stringify(newsObj))
        // add to array
        if (articleList === undefined || articleList === null) {
            articleList = [];
        }
        articleList.push(articleKey)
        localStorage.setItem('favoriteArticles', JSON.stringify(articleList))
        this.setState({
            bookmarked: true,
        })
        toast("Saving " + this.state.result.title, {
            className: 'font-black'
        });
    }
    removeFromFavorites() {
        ReactTooltip.hide()
        let articleList = JSON.parse(localStorage.getItem('favoriteArticles'));
        const articleKey = this.state.result.id.toString()
        // remove from array
        const index = articleList.indexOf(articleKey);
        if (index !== -1) articleList.splice(index, 1);
        localStorage.setItem('favoriteArticles', JSON.stringify(articleList))
        // remove from localstorage
        localStorage.removeItem(articleKey)
        this.setState({
            bookmarked: false,
        })
        toast("Removing - " + this.state.result.title, {
            className: 'font-black'
        });
    }
    render() {
        if (this.state.result !== undefined && this.state.result !== null && this.state.result.length !== 0) {
            var date = format(new Date(this.state.result.date), 'd MMMM yyyy')
            var [basicDesc, advancedDesc] = formatDescription(this.state.result.description)
            var idVal = this.state.result.id.toString();
        }

        return (
            <div>
                <ToastContainer position={toast.POSITION.TOP_CENTER} transition={Zoom} autoclose={3000} hideProgressBar={true} className={"toast-message"} closeOnClick={false} />
                {
                    this.state.result.length === 0
                    ?
                    <LoadingComponent />
                    :
                    <div>
                        <Container fluid className="detialed">
                                <Row className="detailed-title">
                                    <Col>{this.state.result.title}</Col>
                                </Row>
                            <Row className="bottom-padding">
                                <Media queries={{
                                    small: "(max-width: 342px)",
                                    large: "(min-width: 343px)"
                                }}>
                                    {matches => (
                                        <Fragment>
                                            {matches.small &&
                                                <>
                                                    <Col xs={5} md={2} className="detailed-card-date-small">{date}</Col>
                                                    <Col xs={4} md={9} className="detailed-share no-padding">
                                                        <FbShareComponent newsUrl={this.state.result.link} size={25} className={"share-buttons"} /><ReactTooltip id="tooltip-facebook" place={"top"} effect={"solid"} />
                                                        <TwitterShareComponent newsUrl={this.state.result.link} size={25} className={"share-buttons"} /><ReactTooltip id="tooltip-twitter" place={"top"} effect={"solid"} />
                                                        <EmailShareComponent newsUrl={this.state.result.link} size={25} className={"share-buttons"} /><ReactTooltip id="tooltip-email" place={"top"} effect={"solid"} />
                                                    </Col>
                                                    <Col xs={3} md={1}>
                                                        {
                                                            localStorage.getItem(idVal) !== null || this.state.bookmarked === true
                                                            ?
                                                            <span className="detailed-right bookmark-right-padding">
                                                                <IconContext.Provider value={{ size: "20px", color: "red" }}>
                                                                    <FaBookmark onClick={this.removeFromFavorites} data-tip="Bookmark" data-for="tooltip-bookmarked" data-class="padding" /><ReactTooltip id="tooltip-bookmarked" place={"top"} effect={"solid"} />
                                                                </IconContext.Provider>
                                                            </span>

                                                            :
                                                            <span className="detailed-right bookmark-right-padding">
                                                                <IconContext.Provider value={{ size: "20px", color: "red" }}>
                                                                    <FaRegBookmark onClick={this.addToFavorites} data-tip="Bookmark" data-for="tooltip-bookmark" data-class="padding" /><ReactTooltip id="tooltip-bookmark" place={"top"} effect={"solid"} />
                                                                </IconContext.Provider>
                                                            </span>
                                                        }
                                                    </Col>
                                                </>
                                            }
                                            {matches.large &&
                                                <>
                                                    <Col xs={5} md={2} className="detailed-card-date">{date}</Col>
                                                    <Col xs={4} md={9} className="detailed-share">
                                                        <FbShareComponent newsUrl={this.state.result.link} size={27} className={"share-buttons"} /><ReactTooltip id="tooltip-facebook" place={"top"} effect={"solid"} />
                                                        <TwitterShareComponent newsUrl={this.state.result.link} size={27} className={"share-buttons"} /><ReactTooltip id="tooltip-twitter" place={"top"} effect={"solid"} />
                                                        <EmailShareComponent newsUrl={this.state.result.link} size={27} className={"share-buttons"} /><ReactTooltip id="tooltip-email" place={"top"} effect={"solid"} />
                                                    </Col>
                                                    <Col xs={3} md={1}>
                                                        {
                                                            localStorage.getItem(idVal) !== null || this.state.bookmarked === true
                                                            ?
                                                            <span className="detailed-right bookmark-right-padding">
                                                                <IconContext.Provider value={{ size: "24px", color: "red" }}>
                                                                    <FaBookmark onClick={this.removeFromFavorites} data-tip="Bookmark" data-for="tooltip-bookmarked" data-class="padding" /><ReactTooltip id="tooltip-bookmarked" place={"top"} effect={"solid"} />
                                                                </IconContext.Provider>
                                                            </span>
                                                            :
                                                            <span className="detailed-right bookmark-right-padding">
                                                                <IconContext.Provider value={{ size: "24px", color: "red" }}>
                                                                    <FaRegBookmark onClick={this.addToFavorites} data-tip="Bookmark" data-for="tooltip-bookmark" data-class="padding" /><ReactTooltip id="tooltip-bookmark" place={"top"} effect={"solid"} />
                                                                </IconContext.Provider>
                                                            </span>
                                                        }
                                                    </Col>
                                                </>
                                            }
                                        </Fragment>
                                    )}
                                </Media>
                            </Row>
                            <Row>
                                <Col><img className="detailed-image" src={this.state.result.image} alt="detailed page" /></Col>
                            </Row>
                            <Row className="detailed-desc">
                                <Col>
                                    {basicDesc}
                                    <div ref={this.advDescRef} >
                                        <Collapse in={this.state.extended}>
                                            <span className="detailed-desc-adv"><br />{advancedDesc}</span>
                                        </Collapse>
                                    </div>
                                    <br />
                                    {
                                        advancedDesc !== undefined && advancedDesc !== null && advancedDesc.length !== 0
                                        ?
                                        (
                                            this.state.extended
                                                ?
                                                <IconContext.Provider value={{ size: "1em" }} >
                                                    <FaChevronUp onClick={this.handleClick.bind(this, "up")} className="extend-right" />
                                                </IconContext.Provider>
                                                :
                                                <IconContext.Provider value={{ size: "1em" }} className="extend-right">
                                                    <FaChevronDown onClick={this.handleClick.bind(this, "down")} className="extend-right" />
                                                </IconContext.Provider>
                                        )
                                        :
                                        <></>
                                    }
                                </Col>
                            </Row>
                        </Container>
                        <Container fluid>
                            <Row>
                                <Col md={12}><CommentBoxComponent newsId={this.state.result.id} /></Col>
                            </Row>
                        </Container>
                    </div>
                }
            </div>
        )
    }
}

DetailedComponent.propTypes = {
    history : PropTypes.object.isRequired,
    location : PropTypes.object.isRequired,
    handleUpArrowClick : PropTypes.func.isRequired,
}

export default DetailedComponent