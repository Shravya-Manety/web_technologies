import React from 'react'
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Image from 'react-bootstrap/Image'
import { IconContext } from "react-icons";
import { MdShare } from 'react-icons/md';
import { withRouter } from 'react-router-dom';
import PropTypes from 'prop-types'

import './component.css'
import ShareModalComponent from './ShareModalComponent'
import { getSectionClassName } from './Utilties'

class SearchCardComponent extends React.Component {
    
    constructor(props) {
        super(props)
        this.state = {
            show: false,
        }
    }
    sectionName = this.props.section.toUpperCase()
    sectionClass = getSectionClassName(this.sectionName)
    basicClass = "section-basic section-right"
    handleClick(url) {
        this.props.history.push({
            pathname: '/article',
            search: '?id=' + url,
            state : { 
                isGuardian : this.props.isGuardian,
                section : this.sectionName,
            }
        })
    }
    handleModalOpen=(e)=>{
        e.stopPropagation();
        this.setState(() => {
            return {
                show: true
            }
        })
    }
    handleModalClose=()=>{
        this.setState(()=>{
            return{
                show : false
            }
        })
    }
    render() {
        return (
            <>
                <ShareModalComponent show={this.state.show} handleModalClose={()=>this.handleModalClose()} title={this.props.title} link={this.props.link}/>
                <Container fluid className="search-card" onClick={this.handleClick.bind(this, this.props.id)}>
                    <Row>
                        <Col className="search-card-title">
                            {this.props.title}
                            <IconContext.Provider value={{ size: "20px" }}>
                                <MdShare onClick={(e) => this.handleModalOpen(e)}/>
                            </IconContext.Provider>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Image src={this.props.imgUrl} alt="news img" thumbnail />
                        </Col>
                    </Row>
                    <Row className="search-card-date-section">
                        <Col xs={5} md={4} className="search-card-date">{this.props.date}</Col>
                        {/* <Col md={4}></Col> */}
                        <Col xs={7} md={8} ><span className={`${this.sectionClass} ${this.basicClass}`}>{this.sectionName}</span></Col>
                    </Row>
                </Container>
            </>

        )
    }
}
SearchCardComponent.propTypes = {
    section : PropTypes.string.isRequired,
    isGuardian : PropTypes.bool.isRequired,
    description : PropTypes.string.isRequired,
    imgUrl : PropTypes.string.isRequired,
    title : PropTypes.string.isRequired,
    id : PropTypes.string.isRequired,
    history : PropTypes.object.isRequired,
    link : PropTypes.string.isRequired,
}
export default withRouter(SearchCardComponent)