import React, { Fragment } from 'react'
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Image from 'react-bootstrap/Image'
import { IconContext } from "react-icons";
import { MdShare, MdDelete } from 'react-icons/md';
import { withRouter } from 'react-router-dom';
import Media from 'react-media'
import PropTypes from 'prop-types'

import './component.css'
import ShareModalComponent from './ShareModalComponent'
import { getSectionClassName } from './Utilties'
import { format } from 'date-fns'

class FavoriteCardComponent extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            show: false,
        }
    }
    sectionName = this.props.newsObj.section.toUpperCase()
    sectionClass = getSectionClassName(this.sectionName)
    basicClass = "section-basic section-right"
    basicClassSmallScreen = "section-basic section-right section-font-small-screen"
    newsTab = this.props.newsObj.isGuardian ? "guardian-tab" : "nytimes-tab"
    handleClick(url) {
        this.props.history.push({
            pathname: '/article',
            search: '?id=' + url,
            state: { isGuardian: this.props.newsObj.isGuardian }
        })
    }
    handleModalOpen = (e) => {
        e.stopPropagation();
        this.setState(() => {
            return {
                show: true
            }
        })
    }
    handleModalClose = () => {
        this.setState(() => {
            return {
                show: false
            }
        })
    }
    render() {
        const date = format(new Date(this.props.newsObj.date), 'yyyy-MM-dd')
        return (
            <>
                <ShareModalComponent show={this.state.show} handleModalClose={() => this.handleModalClose()} title={this.props.newsObj.title} link={this.props.newsObj.link} isGuardian={this.props.newsObj.isGuardian} />
                <Container fluid className="search-card" onClick={this.handleClick.bind(this, this.props.newsObj.id)}>
                    <Row>
                        <Col className="search-card-title">
                            {this.props.newsObj.title + " "}
                            <IconContext.Provider value={{ size: "20px" }}>
                                <MdShare onClick={(e) => this.handleModalOpen(e)} />
                            </IconContext.Provider>
                            &nbsp;
                            <IconContext.Provider value={{ size: "20px" }}>
                                <MdDelete onClick={(e) => this.props.handleDeleteFromFavorites(e, this.props.newsObj.id, this.props.newsObj.title)} />
                            </IconContext.Provider>

                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Image src={this.props.newsObj.image} alt="news img" thumbnail />
                        </Col>
                    </Row>
                    <Row className="search-card-date-section">
                        <Media queries={{
                            small: "(max-width: 336px)",
                            large: "(min-width: 337px)"
                        }}>
                            {matches => (
                                <Fragment>
                                    {matches.small &&
                                        <>
                                            <Col xs={5} md={4} className="search-card-date">{date}</Col>
                                            <Col xs={7} md={8} >
                                                <span className={`${this.newsTab} ${this.basicClassSmallScreen}`}>{this.props.newsObj.isGuardian ? `GUARDIAN` : `NYTIMES`}</span>
                                                <span className={`${this.sectionClass} ${this.basicClassSmallScreen}`}>{this.sectionName}</span>&nbsp;
                                            </Col>
                                        </>
                                    }
                                    {matches.large &&
                                        <>
                                            <Col xs={4} md={4} className="search-card-date">{date}</Col>
                                            <Col xs={8} md={8} >
                                                <span className={`${this.newsTab} ${this.basicClass}`}>{this.props.newsObj.isGuardian ? `GUARDIAN` : `NYTIMES`}</span>
                                                <span className={`${this.sectionClass} ${this.basicClass}`}>{this.sectionName}</span>&nbsp;
                                            </Col>
                                        </>
                                    }
                                </Fragment>
                            )}
                        </Media>
                    </Row>
                </Container>
            </>
        )
    }
}
FavoriteCardComponent.propTypes = {
    newsObj: PropTypes.object.isRequired,
    handleDeleteFromFavorites: PropTypes.func.isRequired,
    history: PropTypes.object.isRequired,
}
export default withRouter(FavoriteCardComponent)