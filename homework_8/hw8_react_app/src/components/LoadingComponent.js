import React from 'react'
import { css } from "@emotion/core";
import BounceLoader from "react-spinners/BounceLoader";

import './component.css'

const cssClass = css`
display: inline-block;
`;
function LoadingComponent() {
    return (
        <div className = "center-loading">
            <BounceLoader
                css={cssClass}
                size={27}
                color={"#3333ff"}
                loading={true}
            />
            <p>Loading</p>
        </div>
    )
}

export default LoadingComponent