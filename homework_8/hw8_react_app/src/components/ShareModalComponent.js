/* eslint-disable react/prop-types */
import React from 'react'
import { Modal } from 'react-bootstrap'
import FbShareComponent from './FbShareComponent'
import TwitterShareComponent from './TwitterShareComponent'
import EmailShareComponent from './EmailShareComponent'
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'

function ShareModalComponent(props){
    return(
        <Modal show={props.show} onHide={()=>props.handleModalClose()}>
                    <Modal.Header closeButton>
                        <Modal.Title className="modal-title">
                            {
                                props.isGuardian !== undefined
                                ?
                                <p className="newsType-heading">{props.isGuardian ? `GUARDIAN` : `NYTIMES`}</p>
                                :
                                <></>
                            }
                            {props.title}
                        </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Container>
                            <Row className="center-align">
                                <Col className="share-via">Share Via</Col>
                            </Row>
                            <Row className="center-align">
                                <Col>
                                    <code><FbShareComponent newsUrl={props.link} size={45} className={"share-buttons"} /></code>
                                </Col>
                                <Col>
                                    <code><TwitterShareComponent newsUrl={props.link} size={45} className={"share-buttons"} /></code>
                                </Col>
                                <Col>
                                    <code><EmailShareComponent newsUrl={props.link} size={45} className={"share-buttons"} /></code>
                                </Col>
                            </Row>
                        </Container>
                    </Modal.Body>
                </Modal>
    )
}
export default ShareModalComponent