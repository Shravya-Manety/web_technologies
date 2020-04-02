/* eslint-disable react/prop-types */
import React from 'react'
import {FacebookShareButton, FacebookIcon} from 'react-share'

const FbShareComponent = (props) => (
    <div className = {props.className}>
            <FacebookShareButton
            url={props.newsUrl}
            hashtag={"#CSCI_571_NewsApp"}
            data-tip = "Facebook"
            data-for="tooltip-facebook"
            data-class="padding"
            >
            <FacebookIcon size={props.size} round />
          </FacebookShareButton>
        </div>
);

export default FbShareComponent