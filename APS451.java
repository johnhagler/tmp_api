/*
***************************************************************
*                                                             *
*                           NOTICE                            *
*                                                             *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
*   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
*   ALL OTHER RIGHTS RESERVED.                                *
*                                                             *
*   (c) COPYRIGHT 2013 INFOR.  ALL RIGHTS RESERVED.           *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
*                                                             *
***************************************************************
*/
package mvx.app.pgm;

import mvx.app.common.*;
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.dsp.common.GenericDSP;
import mvx.dsp.obj.*;
import mvx.util.*;

/*
*Modification area - M3
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>Wrk: Supplier invoice batch - lines</FONT></B><BR><BR>
*
* This interactive program manages Invoice batch number - lines by calling function program APS451Fnc.
* <BR><BR>
*<PRE>
*<B>Use of indicators on B-panel</B>
* 21         Controls display of 
* 31         Used to enable 'F17-recreate VAT' for Self Billing invoices.
*</PRE>
*/
public class APS451 extends Interactive
{

   /**
   * B-panel - initiate.
   */
   public void PBINZ() {
      ensureReadOfTables();
      // Forget notifications
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      // Set last panel
      lastPanel = 'B';
      // Position cursor
      if (!isBookmarkProcessing()) {
         if (IN61) {
            DSP.setFocus("W1TRNO");
         }
         if (IN62) {
            DSP.setFocus("W2RDTP");
         }
         if (IN63) {
            DSP.setFocus("W3ITNO");
         }
         if (IN64) {
            DSP.setFocus("W4SUDO");
         }
      }
      // Init new Sorting order 	 	
      if (!isBookmarkProcessing()) {
         if (DSP.WWQTTP != XXQTTP) { 	 	
            if (!CRMNGVW.setStandardFromSortingOrder(DSP.WWQTTP, SYVIU, SYVIP, SYSOR)) { 	 	
               // Sorting order not found 	 	
               picSetMethod('D'); 	 	 	 	
               IN60 = true; 	 	 	 	
               DSP.setFocus("WWQTTP");
               // MSGID=WQT0101 Sorting order &1 does not exist 	 	 	
               COMPMQ("WQT0101", formatToString(DSP.WWQTTP, 2)); 	 	 	
               return; 	 	 	 	
            } 	
            CRMNGVW.setDefaultValues(DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, DSP.WWPSEQ, SYSPV, SYSPP, 'B', DSP.WWQTTP, startView, startPanelSequence);
         } 	 	
         // - Init new View 	 	 	 	
         if (CRMNGVW.initView(DSP.WWQTTP, SYSPV, DSP.WOPAVR, DSP.WOUPVR, this.DSUSS)) { 	 	 	 	
            if (CRMNGVW.PXIN60) { 	 	 	 	
               picSetMethod('D'); 	 	 	
               IN60 = true; 	 	 	
               DSP.setFocus("WOPAVR"); 	 	 	
               COMPMQ(formatToString(CRMNGVW.MSGID), formatToString(DSP.WOPAVR)); 	 	 	 	
               return; 	 	 	
            } 	 	 	
            subfHead(); 	 	
         } 	 	 	 	
      }
      XXPAVR.move(DSP.WOPAVR); 	 	 	 	
      XXUPVR.move(DSP.WOUPVR);
      XXQTTP = DSP.WWQTTP;
      // Init of subfile information
      // - Supplier invoice number
      DSP.WBSINO.moveLeftPad(APIBH.getSINO());
      // - Payee
      DSP.WBSPYN.moveLeftPad(APIBH.getSPYN());
      // - Supplier
      DSP.WBSUNO.moveLeftPad(APIBH.getSUNO());
      // - Invoice date
      DSP.WBIVDT.moveLeft(CRCalendar.convertDate(DSP.WBDIVI, APIBH.getIVDT(), LDAZD.DTFM, ' '));
      // - Currency amount
      this.PXDCCD = cRefCUAM.decimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = APIBH.getCUAM();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WBCUAM.moveRight(this.PXALPH);
      // - Currency
      DSP.WBCUCD.moveLeftPad(APIBH.getCUCD());
      // - Invoice status
      DSP.WBSUPA = APIBH.getSUPA();
      // - Correlation ID, hidden field used for link to Document archive
      DSP.WWCORI.moveLeftPad(APIBH.getCORI());
      X6RDTP = DSP.WBRDTP;
      X6IBLE = DSP.WBIBLE;
      if (IN61) {
         X6TRNO = DSP.W1TRNO;
      }
      if (IN62) {
         X6RDTP = DSP.W2RDTP;
         X6TRNO = DSP.W2TRNO;
      }
      if (IN63) {
         X6ITNO.moveLeftPad(DSP.W3ITNO);
         X6TRNO = DSP.W3TRNO;
      }
      if (IN64) {
         X6SUDO.moveLeftPad(DSP.W4SUDO);
         X6TRNO = DSP.W4TRNO;
      }
      XLTRNO = 0;
      XLRDTP = 0;
      XLITNO.clear();
      XLSUDO.clear();
      this.WSRGTM = movexDateTime();
      DSP.XBRRNA = 0;
      DSP.XBRRNM = 0;
      if (isBookmarkProcessing()) {
         // Do not fill the subfile at this stage
         return;
      }
      // Clear the subfile
      IN94 = true;
      IN95 = false;
      DSP.clearSFL("BC");
      IN94 = false;
      IN95 = false;
      IN96 = false;
      // Set limit & build subfile, Sorting order 1
      if (IN61) {
         APIBL.setTRNO(DSP.W1TRNO);
         APIBL.SETLL_SCAN("00", APIBL.getKey("00"));
      }
      // Set limit & build subfile, Sorting order 2
      if (IN62) {
         APIBL.setRDTP(DSP.W2RDTP);
         APIBL.setTRNO(DSP.W2TRNO);
         APIBL.SETLL_SCAN("10", APIBL.getKey("10"));
      }
      // Set limit & build subfile, Sorting order 3
      if (IN63) {
         APIBL.setITNO().moveLeftPad(DSP.W3ITNO);
         APIBL.setTRNO(DSP.W3TRNO);
         APIBL.SETLL_SCAN("20", APIBL.getKey("20"));
      }
      // Set limit & build subfile, Sorting order 4
      if (IN64) {
         APIBL.setSUDO().moveLeftPad(DSP.W4SUDO);
         APIBL.setTRNO(DSP.W4TRNO);
         APIBL.SETLL_SCAN("30", APIBL.getKey("30"));
      }
      PBINZ_SETLL(); // exit point for modification
      PBROL();
   }

   /**
   * B-panel - display - initiate.
   */
   public void PBDSP_INZ() {
      // Set last panel
      lastPanel = 'B';
      // Restore panel sequence after setSpecialPanelSequence
      CRCommon.restorePanelSequence(saveSEQ);
      // Division - only display for central user
      IN21 = !CRCommon.isCentralUser();
      // Enable F17 - Recreate VAT
      IN31 = APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING());
   }

   /**
   * B-panel - display - get next Standard view.
   */
   public void PBDSP_F10() { 	
      CRMNGVW.nextStandardView(this, DSP.WOPAVR, DSP.WOUPVR, SYSPV, 'B'); 	 	
      return; 	
   }

   /**
   * Recalculate VAT
   */
    public void PBDSP_F17() {
      PPSBIVATDS.clear();
      PPSBIVATDS.setPWCONO(APIBH.getCONO());
      PPSBIVATDS.setPWDIVI().move(APIBH.getDIVI());
      PPSBIVATDS.setPWINBN(APIBH.getINBN());
      PPSBIVATDS.setPWUSS().move(this.DSUSS);
      rSBIVAT.reset();
      rSBIVAT.set(PPSBIVATDS.getPPSBIVATDS());
      apCall("PPSBIVAT", rSBIVAT);
      rSBIVAT.reset();
      rSBIVAT.getString(PPSBIVATDS.setPPSBIVATDS());
      picSetMethod('I');
      return;
   }
  
   /**
   * B-panel - check.
   */
   public void PBCHK() {
      ensureReadOfTables();
      DSP.WBRRNA = 0;
      PBCHK_process();
      PBCHK_ExecuteOption(); // exit point for modification
      // Clear option in case of error
      if (DSP.WBRRNA != 0 && IN60) {
         DSP.clearOption();
         DSP.XBRRNA = DSP.WBRRNA;
         DSP.updateSFL("BS");
      }
   }

   /**
   * B-panel - check - validate and process user action.
   *
   * Turns on IN60 if an error was found.
   */
   public void PBCHK_process() {
      // Clear list update indicator
      boolean listUpdate = false;
      // Check if View created/updated by UI. In that case a refresh is needed 	 	
      CRMNGVW.checkUIAction(DSP, refreshReason, XXPAVR, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR,  SYSPV, SYSPP, 'B', DSP.WWQTTP); 	
      // Check sorting order
      if (PBCHK_ValidateInquiry()) {  // exit point for modification
         if (IN60) {
            return;
         }
      } else {
         if (!validSortingOrder(DSP.WWQTTP)) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WWQTTP");
            // MSGID=WQT0101 Sorting order &1 is invalid
            COMPMQ("WQT0101", formatToString(DSP.WWQTTP, 2));
            return;
         }
      }
      CRMNGVW.checkSorting(DSP.WWQTTP, SYVIU); 	 	
      if (CRMNGVW.PXIN60) { 	 	
         picSetMethod('D'); 	 	
         IN60 = true; 	 	
         DSP.setFocus("WWQTTP"); 	 	
         return; 	 	 	 	
      } 	
      // Set sorting order
      if (!getIN(60 + DSP.WWQTTP)) {
         setIndicatorForSortingOrder(DSP.WWQTTP);
         picSetMethod('I');
         return;
      }
      // Check View 	 	
      if (DSP.WOPAVR.NE(XXPAVR) || 	 	 	
          DSP.WOUPVR.NE(XXUPVR) || 	 	
          (DSP.WOPAVR.isBlank() && DSP.WOUPVR.isBlank())) { 	 	 	
         CRMNGVW.PGNM.move(this.DSPGM); 	 	 	 	
         CRMNGVW.checkView(DSP.WWQTTP, SYSPV, DSP.WOPAVR, DSP.WOUPVR); 	 	 	 	 	
         if (CRMNGVW.PXIN60) { 	 	 	 	
            picSetMethod('D'); 	 	
            IN60 = true; 	 	
            DSP.setFocus("WOPAVR"); 	 	 	
            if (CRMNGVW.MSGID.EQ("WPA1103")) { 	 	
               // MSGID=WPA1103 View must be entered 	 	
               COMPMQ("WPA1103"); 	 	
            } else { 	 	
               COMPMQ(formatToString(CRMNGVW.MSGID), formatToString(DSP.WOPAVR)); 	 	 	 	
            } 	
            return; 	 	
         } 	 	
         picSetMethod('I'); 	 	
         return; 	 	 	 	
      }
      // Check panel sequence
      if (!seqCheckSeq(getAllowedPanels(), DSP.WWPSEQ)) {
         return;
      }
      this.CSSQ.moveLeft(this.SEQ);
      // Check list head fields
      // - TODO
      // Get option and set primary keys
      if (isBookmarkProcessing()) {
         setKeyFieldsInPBCHKFromBookmark();
      } else {
         // Option and keys from subfile head
         DSP.WSOPT2.clear();
         APIBL.setDIVI().moveLeftPad(DSP.WBDIVI);
         APIBL.setINBN(DSP.WBINBN);
         if (IN61) {
            DSP.WSOPT2.move(DSP.WBOPT2);
            APIBL.setTRNO(DSP.W1TRNO);
         }
         if (IN62) {
            DSP.WSOPT2.move(DSP.WBOPT2);
            APIBL.setRDTP(DSP.W2RDTP);
            APIBL.setTRNO(DSP.W2TRNO);
         }
         if (IN63) {
            DSP.WSOPT2.move(DSP.WBOPT2);
            APIBL.setITNO().moveLeftPad(DSP.W3ITNO);
            APIBL.setTRNO(DSP.W2TRNO);
         }
         if (IN64) {
            DSP.WSOPT2.move(DSP.WBOPT2);
            APIBL.setSUDO().moveLeftPad(DSP.W4SUDO);
            APIBL.setTRNO(DSP.W4TRNO);
         }
      }
      // - option and keys from subfile
      if (DSP.WSOPT2.isBlank() && DSP.XBRRNM != 0) {
         boolean endOfSfl = false;
         do {
            DSP.WBRRNA = 0;
            endOfSfl = DSP.readSFL("BS");
            if (!endOfSfl) {
               APIBL.setDIVI().moveLeftPad(DSP.S0DIVI);
               APIBL.setINBN(DSP.S0INBN);
               APIBL.setTRNO(DSP.S0TRNO);
               if (toBoolean(DSP.WSCHG) && DSP.WSOPT2.NE("2 ") && DSP.WSOPT2.NE(" 2") ) {
                  // Field changed in subfile line
                  listUpdate = true;
               }
            }
         } while (DSP.WSOPT2.isBlank() && !endOfSfl && !listUpdate);
      }
      if (pMaintain != null && !listUpdate) {
         pMaintain.messages.forgetNotifications();
      }
      // New start position
      if (DSP.WSOPT2.isBlank() && !listUpdate) {
         if (IN61) {
            if (DSP.W1TRNO != X6TRNO) {
               picSetMethod('I');
               return;
            }
         }
         if (IN62) {
            if (DSP.W2RDTP != X6RDTP ||
                DSP.W2TRNO != X6TRNO) {
               picSetMethod('I');
               return;
            }
         }
         if (IN63) {
            if (DSP.W3ITNO.NE(X6ITNO) ||
                DSP.W3TRNO != X6TRNO) {
               picSetMethod('I');
               return;
            }
         }
         if (IN64) {
            if (DSP.W4SUDO.NE(X6SUDO) ||
                DSP.W4TRNO != X6TRNO) {
               picSetMethod('I');
               return;
            }
         }
         PBCHK_CheckStartPosition(); // exit point for modification
      }
      // No option given
      if (DSP.WSOPT2.isBlank() && !listUpdate) {
         picSetMethod('D');
         return;
      }
      // Check option - adjust option and check if authorized
      if (listUpdate) {
         this.XXOPT2.move(" 2");
         if (PxCHKoption()) {
            return;
         }
      }
      XXOPT2.clear();
      if (!DSP.WSOPT2.isBlank()) {
         this.XXOPT2.move(DSP.WSOPT2);
         if (PxCHKoption()) {
            return;
         }
      }
      // Reset picture mode indicators IN41 - IN49
      CRCommon.clearMode();
      // Set panel sequence
      seqLoadSavedSeq();
      // Transfer data to calling program
      if (this.XXOPT2.EQ(" 1") && DSP.WBRRNA > 0 && cCRCommon.checkIfTransferData(this)) {
         Bookmark bookmark = getBookmark();
         bookmark.setRecord("FAPIBL", getPrimaryKeyForTable("FAPIBL"));
         bookmark = null;
         LDAZZ.TPGM.move(this.DSPGM);
         picPush("**");
         return;
      }
      // Check if valid option
      if (!DSP.WSOPT2.isBlank() && !PBCHK_ValidateOption()) { // exit point for modification
         if (this.XXOPT2.LT(" 1")  ||
             this.XXOPT2.GT(" 5") &&
             this.XXOPT2.NE("20"))
         {
            picSetMethod('D');
            IN60 = true;
            if (DSP.WBRRNA == 0) {
               DSP.setFocus("WBOPT2");
            }
            // MSGID=WOP0101 Option &1 is invalid
            COMPMQ("WOP0101", this.XXOPT2);
            return;
         }
      }
      // Check if option (1-9) is authorized
      this.XI = toInt(this.XXOP22.getChar());
      if (this.XI >= 1 && this.XI <= 9 && this.XXOP21.getChar() == ' ' && !toBoolean(this.PXO.charAt(this.XI - 1)) ||
          listUpdate && !toBoolean(this.PXO.charAt(2 - 1)) )
      {
         picSetMethod('D');
         IN60 = true;
         if (DSP.WBRRNA == 0) {
            DSP.setFocus("WBOPT2");
         }
         if (listUpdate) {
            setFocus_on_subfile_B(DSP.WBRRNA);
         }
         // MSGID=XAU0001 You do not have authorization for the selected option
         COMPMQ("XAU0001");
         return;
      }
      // Check key values
      if (this.XXOPT2.EQ(" 1")) {
         // Set next transaction number
         APIBL.SETGT("00", APIBL.getKey("00", 3));
         if (!APIBL.REDPE("00", APIBL.getKey("00", 3))) {
            APIBL.setTRNO(1);
         } else {
            APIBL.setTRNO(APIBL.getTRNO() + 1);
         }
      } else {
         if (APIBL.getTRNO() == 0) {
            picSetMethod('D');
            IN60 = true;
            if (IN61) {
               DSP.setFocus("W1TRNO");
            }
            if (IN62) {
               DSP.setFocus("W2RDTP");
            }
            if (IN63) {
               DSP.setFocus("W3ITNO");
            }
            if (IN64) {
               DSP.setFocus("W4SUDO");
            }
            // MSGID=WTR3102 Transaction number must be entered
            COMPMQ("WTR3102");
            return;
         }
      }
      // Check record
      found_FAPIBL = APIBL.CHAIN("00", APIBL.getKey("00"));
      passFAPIBL = found_FAPIBL;
      IN91 = !found_FAPIBL;
      if (isBookmarkProcessing()) { 	
         validateRecordInPBCHKInCaseOfBookmark(); 	
         if (IN60) { 	
            return;
         } 	
      } 	
      // Add
      if (seqAddModeOk(getPanelsRequiredForAdd())) {
         if (CRCommon.getMode() == cEnumMode.ADD) {
            CRCommon.addPanelsFromSelectedPanelSequence();
            return;
         }
      } else {
         if (DSP.WBRRNA == 0) {
            if (IN61) {
               DSP.setFocus("W1TRNO");
            }
            if (IN62) {
               DSP.setFocus("W2RDTP");
            }
            if (IN63) {
               DSP.setFocus("W3ITNO");
            }
            if (IN64) {
               DSP.setFocus("W4SUDO");
            }
         }
         // MSGID=WTR3104 Transaction number &1 already exists
         COMPMQ("WTR3104", formatToString(APIBL.getTRNO(), cRefTRNO.length()));
         return;
      }
      // Check if exist
      if (!found_FAPIBL) {
         if (listUpdate) {
            setFocus_on_subfile_B(DSP.WBRRNA);
         }
         picSetMethod('D');
         IN60 = true;
         // MSGID=WTR3103 Transaction number &1 does not exist
         COMPMQ("WTR3103", formatToString(APIBL.getTRNO(), cRefTRNO.length()));
         return;
      }
      // Handle update in list
      if (listUpdate) {
         CRCommon.setChangeMode();
         if (updateFromSflB()) {
            pMaintain.messages.forgetNotifications();
            // Prepare to check next subfile line
            picSetMethod('C');
            // Update subfile
            lastPanel = 'B'; // Set last panel
            setFocus_on_subfile_B(DSP.WBRRNA);
            moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
            DSP.clearOption();
            DSP.XBRRNA = DSP.WBRRNA;
            PBMSF();
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
            DSP.updateSFL("BS");
            CRCommon.clearMode();
         } else {
            IN60 = true; 	
            picSetMethod('D');  // Display message
            return;
         }
      }
      // Change / Copy / Delete / Display
      if (!seqChangeModeOk() ||
          !seqCopyModeOk() ||
          !seqDeleteModeOk() ||
          !seqDisplayModeOk()) 
      {
         // MSGID=WTR3103 Transaction number &1 does not exist
         COMPMQ("WTR3103", formatToString(APIBL.getTRNO(), cRefTRNO.length()));
         return;
      }
      // Don't display or change if no panels in panel sequence
      if (XXOPT2.EQ(" 2") || XXOPT2.EQ(" 5")) {
         if (picGetPanel() == ' ' || picGetPanel() == '-') {
            picPop();
         }
      }
      if (this.XXOPT2.EQ("20")) {
         // Approve Invoice
         picSetMethod('I');  // Rebuild subfile to show updated VAT
         CRCommon.setSpecialPanelSequence("M", saveSEQ);
         picPush(this.SEQ.charAt(0), 'I');
         return;
      }
   }
   
   /**
   * B-panel - update.
   */
   public void PBUPD() {
      ensureReadOfTables();
      lastPanel = 'B'; // Set last panel
      moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
      DSP.clearOption();
      // Restore panel sequence after setSpecialPanelSequence
      CRCommon.restorePanelSequence(saveSEQ);
      // Next step
      if (DSP.WBRRNA != 0) {
         picSetMethod('C');
      } else {
         picSetMethod('I');
      }
      // No subfile record
      if (DSP.WBRRNA == 0) {
         return;
      }
      // F12 was pressed - stop processing subfile lines
      if (F12 == DSP.X0FKEY) {
         picSetMethod('D');
         this.XLPIC1 = picGetPrevTopPanel();
         // Check if copy aborted.
         if (this.XLPIC1 == 'C') {
            DSP.XBRRNA = DSP.WBRRNA;
            DSP.updateSFL("BS");
            return;
         }
      }
      // Update just option on subfile line if display mode
      if (this.XXOPT2.EQ(" 5")) {
         DSP.XBRRNA = DSP.WBRRNA;
         DSP.updateSFL("BS");
         return;
      }
      // Move new values to subfile (if not copy)
      if (this.XXOPT2.NE(" 3")) {
         if (APIBL.CHAIN("00", APIBL.getKey("00"))) {
            DSP.XBRRNA = DSP.WBRRNA;
         } else {
            IN50 = true;
            IN51 = true;
            DSP.XBRRNA = DSP.XBRRNT;
         }
         PBMSF();
         if (this.XXOPT2.EQ(" 2")) {
            DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
         }
         DSP.updateSFL("BS");
         IN50 = false;
         IN51 = false;
         return;
      }
      // Copy
      DSP.updateSFL("BS");
      // Check if copy was completed
      if (APIBL.CHAIN("00", APIBL.getKey("00"))) {
         DSP.XBRRNM++;
         DSP.XBRRNA = DSP.XBRRNM;
         DSP.WBRRNA = DSP.XBRRNM;
         DSP.WSOPT2.clear();
         PBMSF();
         DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
         DSP.writeSFL("BS");
         IN95 = true;
      }
   }

   /**
   * B-panel - roll subfile.
   */
   public void PBROL() {
      ensureReadOfTables();
      DSP.XBRRNA = DSP.XBRRNM;
      DSP.restPosSFL('B');
      // Restore last position
      if (DSP.XBRRNM > 0) {
         APIBL.setDIVI().moveLeftPad(XLDIVI);
         APIBL.setINBN(XLINBN);
         if (IN61) {
            APIBL.setTRNO(XLTRNO);
            APIBL.SETGT_SCAN("00", APIBL.getKey("00"));
         }
         if (IN62) {
            APIBL.setRDTP(XLRDTP);
            APIBL.setTRNO(XLTRNO);
            APIBL.SETGT_SCAN("10", APIBL.getKey("10"));
         }
         if (IN63) {
            APIBL.setITNO().moveLeftPad(XLITNO);
            APIBL.setTRNO(XLTRNO);
            APIBL.SETGT_SCAN("20", APIBL.getKey("20"));
         }
         if (IN64) {
            APIBL.setSUDO().moveLeftPad(XLSUDO);
            APIBL.setTRNO(XLTRNO);
            APIBL.SETGT_SCAN("30", APIBL.getKey("30"));
         }
         PBROL_SETGT(); // exit point for modification
      }
      if (IN61) {
         found_FAPIBL = APIBL.READE("00", APIBL.getKey("00", 3));
      }
      if (IN62) {
         found_FAPIBL = APIBL.READE("10", APIBL.getKey("10", 3));
      }
      if (IN63) {
         found_FAPIBL = APIBL.READE("20", APIBL.getKey("20", 3));
      }
      if (IN64) {
         found_FAPIBL = APIBL.READE("30", APIBL.getKey("30", 3));
      }
      PBROL_READE1(); // exit point for modification
      while (found_FAPIBL && this.XXSPGB > 0) {
         if (APIBL.isQualifiedForSFL(WSRGTM)) {
            if (includeRecord()) {
               this.XXSPGB--;
               DSP.XBRRNA++;
               DSP.WBRRNA = DSP.XBRRNA;
               DSP.XBRRNM++;
               XLDIVI.moveLeftPad(APIBL.getDIVI());
               XLINBN = APIBL.getINBN();
               XLTRNO = APIBL.getTRNO();
               XLRDTP = APIBL.getRDTP();
               XLITNO.moveLeftPad(APIBL.getITNO());
               XLSUDO.moveLeftPad(APIBL.getSUDO());
               DSP.WSOPT2.clear();
               PBROL_PBMSF(); // exit point for modification
               PBMSF();
               DSP.WSCHG = toChar(false); // Indicate no fields changed on sfl-line
               DSP.writeSFL("BS");
            }
         }
         if (IN61) {
            found_FAPIBL = APIBL.READE("00", APIBL.getKey("00", 3));
         }
         if (IN62) {
            found_FAPIBL = APIBL.READE("10", APIBL.getKey("10", 3));
         }
         if (IN63) {
            found_FAPIBL = APIBL.READE("20", APIBL.getKey("20", 3));
         }
         if (IN64) {
            found_FAPIBL = APIBL.READE("30", APIBL.getKey("30", 3));
         }
      }
      // End of subfile
      PBROL_READE2(); // exit point for modification
      if (!found_FAPIBL) {
         IN96 = true;
         DSP.writeEofSFL("BS");
      }
      // If records exists then display the subfile
      if (DSP.XBRRNM > 0) {
         IN95 = true;
         DSP.XBRRNA = DSP.XBRRNM - 1;
         DSP.trunkPosSFL('B');
         DSP.XBRRNA++;
      }
   }

   /**
   * B-panel - move information to subfile fields.
   */
   public void PBMSF() {
      DSP.moveDSintoSFLpre("B"); // Do standard sync for SFL buf
      PBMSF_AfterSFLPre();  // exit point for modification
      // Set hidden key fields
      DSP.S0DIVI.moveLeftPad(APIBL.getDIVI());
      DSP.S0INBN = APIBL.getINBN();
      DSP.S0TRNO = APIBL.getTRNO();
      // Get info from APS451Fnc
      pMaintain = get_pMaintain();
      pMaintain.messages.forgetNotifications();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.setQuickMode();
      pMaintain.APIBL = APIBL;
      pMaintain.passFAPIBL = true;
      passExtensionTable(); // exit point for modification
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      pMaintain.release();
      // Read tables required in the view
      // - no tables to read
      // Override no of decimals 	 	 	
      setOverridingDecimals(); 	 	 	
      // Create subfile line 	 	 	
      CRMNGVW.line();
      // Protect fields
      if (this.PXO.charAt(1) == '0') {
         // Change authority needed for input
         CRMNGVW.protectAllEditFields();
      } else {
         pMaintain.parameters.prepareGetNext();
         for (cParameter param = pMaintain.parameters.getNext(); param != null; 
            param = pMaintain.parameters.getNext()) 
         {
            if (param.isAccessOUTorDISABLED()) {
               CRMNGVW.protectEditField("E6" + param.getName());
            }
         }
      }
      // Set values in virtual fields (&-fields) 	 	 	
      setVirtualFieldValues(); 	 	 	
      DSP.S0QTTP.moveLeft(CRMNGVW.getLine()); 	 	 	
      moveFromIN(DSP.S0INDI, /*fromInd*/ 1); // Save protect indicators
      DSP.S0CHNO = pMaintain.work_CHNO; // Save change number
      PBMSF_BeforeSFLPost(); // exit point for modification
      DSP.moveDSintoSFLpost("B"); // Do standard sync for SFL buf
   }

   /**
   * B-panel - prompt.
   */
   public boolean PBPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PBPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * B-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PBPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt sorting order 	 	 	 	
      if (DSP.hasFocus("WWQTTP")) { 	 	 	 	
         DSP.WWQTTP = CRMNGVW.promptSortingOrder(this, DSP, "WWQTTP", DSP.WWQTTP, SYVIU); 	 	 	
         return true; 	
      } 	 	 	
      // ----------------------------------------------------------------
      // Prompt view 	 	 	
      if (DSP.hasFocus("WOPAVR")) { 	 	 	
         CRMNGVW.promptView(this, DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, SYSPV, SYSPP, 'B', DSP.WWQTTP); 	 	 	
         return true;
      } 	 	
      // ----------------------------------------------------------------
      return false;
   }

  /**
   * Returns true if record should be included
   * @return true if record should be included
   */
   public boolean includeRecord() {
      if (DSP.WBRDTP != 0) {
         if (DSP.WBRDTP != APIBL.getRDTP()) {
            return false;
         }
      }
      if (DSP.WBIBLE != 0) {
         if (DSP.WBIBLE != APIBL.getIBLE()) {
            return false;
         }
      }
      return true;
   }
   
   /**
   * Updates with values entered on the subfile line.
   * Sets focus to the subfile line if an info message is issued.
   * @return
   *    False if an info message is issued.
   */
   public boolean updateFromSflB() {
      boolean infoMessage = false;
      boolean newEntryContext = false;
      // Restore change number
      APIBL.setCHNO(DSP.S0CHNO); 
      // Call APS451Fnc, maintain - mode CHANGE, step INITIATE
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pMaintain.indicateAutomated();
      // Pass a reference to the record.
      pMaintain.APIBL = APIBL;
      pMaintain.passFAPIBL = true;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Return error message
      if (infoMessage) {
         return false;
      }
      // Call APS451Fnc, maintain - mode CHANGE, step VALIDATE
      // =========================================
      do {
         pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.VALIDATE);
         // Move fields to function parameters
         if (!setParametersFromListB(DSP.WBRRNA)) {
            return false;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS451Fnc", pMaintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
         newEntryContext = pMaintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pMaintain.release();
         // Return error message
         if (infoMessage) {
            return false;
         }
      } while (newEntryContext);
      // Call APS451Fnc, maintain - mode CHANGE, step UPDATE
      // =========================================
      pMaintain.prepare(cEnumMode.CHANGE, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      // =========================================
      // Handle messages
      infoMessage = handleMessagesInListB(DSP.WBRRNA, pMaintain.messages);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Return error message
      if (infoMessage) {
         return false;
      }
      APIBL.CHAIN("00", APIBL.getKey("00"));
      return true;
   }   

   /**
   * Sets parameters in pMaintain from edit-fields in list B.
   * Issues an info message if an error occurs and sets focus to the field.
   *
   * @param lineNo
   *    Line number in the subfile
   * @return
   *    Returns false an error occurs.
   */
   public boolean setParametersFromListB(int lineNo) {
      // Prepare CRMNGVW
      CRMNGVW.setLine(DSP.S0QTTP);
      moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
      setOverridingDecimals();
      // Division
      if (CRMNGVW.hasField("E6DIVI")) {
         if (!CRMNGVW.isProtected("E6DIVI")) {
            pMaintain.DIVI.set().moveLeftPad(CRMNGVW.getString("E6DIVI"));
         }
      }
      // Invoice batch number
      if (CRMNGVW.hasField("E6INBN")) {
         if (!CRMNGVW.isProtected("E6INBN")) {
            pMaintain.INBN.set(CRMNGVW.getLong("E6INBN"));
         }
      }
      // Transaction number
      if (CRMNGVW.hasField("E6TRNO")) {
         if (!CRMNGVW.isProtected("E6TRNO")) {
            pMaintain.TRNO.set(CRMNGVW.getInt("E6TRNO"));
         }
      }
      // Line type
      if (CRMNGVW.hasField("E6RDTP")) {
         if (!CRMNGVW.isProtected("E6RDTP")) {
            pMaintain.RDTP.set(CRMNGVW.getInt("E6RDTP"));
         }
      }
      // Service code
      if (CRMNGVW.hasField("E6SERS")) {
         if (!CRMNGVW.isProtected("E6SERS")) {
            pMaintain.SERS.set(CRMNGVW.getInt("E6SERS"));
         }
      }
      // Net amount
      if (CRMNGVW.hasField("E6NLAM")) {
         if (!CRMNGVW.isProtected("E6NLAM")) {
            pMaintain.NLAM.set(CRMNGVW.getDouble("E6NLAM"));
            if (CRMNGVW.getErrorCode("E6NLAM") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("NLAM");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6NLAM"), 1));
               return false;
            }
         }
      }
      // Purchase order number
      if (CRMNGVW.hasField("E6PUNO")) {
         if (!CRMNGVW.isProtected("E6PUNO")) {
            pMaintain.PUNO.set().moveLeftPad(CRMNGVW.getString("E6PUNO"));
         }
      }
      // VAT amount 1
      if (CRMNGVW.hasField("E6VTA1")) {
         if (!CRMNGVW.isProtected("E6VTA1")) {
            pMaintain.VTA1.set(CRMNGVW.getDouble("E6VTA1"));
            if (CRMNGVW.getErrorCode("E6VTA1") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("VTA1");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6VTA1"), 1));
               return false;
            }
         }
      }
      // PO line.
      if (CRMNGVW.hasField("E6PNLI")) {
         if (!CRMNGVW.isProtected("E6PNLI")) {
            pMaintain.PNLI.set(CRMNGVW.getInt("E6PNLI"));
         }
      }
      // PO line sub number.
      if (CRMNGVW.hasField("E6PNLS")) {
         if (!CRMNGVW.isProtected("E6PNLS")) {
            pMaintain.PNLS.set(CRMNGVW.getInt("E6PNLS"));
         }
      }
      // VAT amount 2
      if (CRMNGVW.hasField("E6VTA2")) {
         if (!CRMNGVW.isProtected("E6VTA2")) {
            pMaintain.VTA2.set(CRMNGVW.getDouble("E6VTA2"));
            if (CRMNGVW.getErrorCode("E6VTA2") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("VTA2");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6VTA2"), 1));
               return false;
            }
         }
      }
      // Invoiced qty
      if (CRMNGVW.hasField("E6IVQA")) {
         if (!CRMNGVW.isProtected("E6IVQA")) {
            pMaintain.IVQA.set(CRMNGVW.getDouble("E6IVQA"));
            if (CRMNGVW.getErrorCode("E6IVQA") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("IVQA");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6IVQA"), 1));
               return false;
            }
         }
      }
      // U/M (Invoiced qty).
      if (CRMNGVW.hasField("E6PUUN")) {
         if (!CRMNGVW.isProtected("E6PUUN")) {
            pMaintain.PUUN.set().moveLeftPad(CRMNGVW.getString("E6PUUN"));
         }
      }
      // VAT code
      if (CRMNGVW.hasField("E6VTCD")) {
         if (!CRMNGVW.isProtected("E6VTCD")) {
            pMaintain.VTCD.set(CRMNGVW.getInt("E6VTCD"));
         }
      }
      // Gross price
      if (CRMNGVW.hasField("E6GRPR")) {
         if (!CRMNGVW.isProtected("E6GRPR")) {
            pMaintain.GRPR.set(CRMNGVW.getDouble("E6GRPR"));
            if (CRMNGVW.getErrorCode("E6GRPR") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("GRPR");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6GRPR"), 1));
               return false;
            }
         }
      }
      // U/M (Gross price).
      if (CRMNGVW.hasField("E6PPUN")) {
         if (!CRMNGVW.isProtected("E6PPUN")) {
            pMaintain.PPUN.set().moveLeftPad(CRMNGVW.getString("E6PPUN"));
         }
      }
      // Self billing agreement number.
      if (CRMNGVW.hasField("E6SBAN")) {
         if (!CRMNGVW.isProtected("E6SBAN")) {
            pMaintain.SBAN.set().moveLeftPad(CRMNGVW.getString("E6SBAN"));
         }
      }
      // Net price
      if (CRMNGVW.hasField("E6NEPR")) {
         if (!CRMNGVW.isProtected("E6NEPR")) {
            pMaintain.NEPR.set(CRMNGVW.getDouble("E6NEPR"));
            if (CRMNGVW.getErrorCode("E6NEPR") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("NEPR");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6NEPR"), 1));
               return false;
            }
         }
      }
      // Sequence no
      if (CRMNGVW.hasField("E6CDSE")) {
         if (!CRMNGVW.isProtected("E6CDSE")) {
            pMaintain.CDSE.set(CRMNGVW.getInt("E6CDSE"));
         }
      }
      // Purchase order qty
      if (CRMNGVW.hasField("E6PUCD")) {
         if (!CRMNGVW.isProtected("E6PUCD")) {
            pMaintain.PUCD.set(CRMNGVW.getInt("E6PUCD"));
         }
      }
      // Costing element.
      if (CRMNGVW.hasField("E6CEID")) {
         if (!CRMNGVW.isProtected("E6CEID")) {
            pMaintain.CEID.set().moveLeftPad(CRMNGVW.getString("E6CEID"));
         }
      }
      // Gross amount
      if (CRMNGVW.hasField("E6GLAM")) {
         if (!CRMNGVW.isProtected("E6GLAM")) {
            pMaintain.GLAM.set(CRMNGVW.getDouble("E6GLAM"));
            if (CRMNGVW.getErrorCode("E6GLAM") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("GLAM");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6GLAM"), 1));
               return false;
            }
         }
      }
      // Receiving number.
      if (CRMNGVW.hasField("E6REPN")) {
         if (!CRMNGVW.isProtected("E6REPN")) {
            pMaintain.REPN.set(CRMNGVW.getLong("E6REPN"));
         }
      }
      // Discount
      if (CRMNGVW.hasField("E6DIPC")) {
         if (!CRMNGVW.isProtected("E6DIPC")) {
            pMaintain.DIPC.set(CRMNGVW.getDouble("E6DIPC"));
            if (CRMNGVW.getErrorCode("E6DIPC") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("DIPC");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6DIPC"), 1));
               return false;
            }
         }
      }
      // Receipt type
      if (CRMNGVW.hasField("E6RELP")) {
         if (!CRMNGVW.isProtected("E6RELP")) {
            pMaintain.RELP.set(CRMNGVW.getInt("E6RELP"));
         }
      }
      // Discount amount
      if (CRMNGVW.hasField("E6DIAM")) {
         if (!CRMNGVW.isProtected("E6DIAM")) {
            pMaintain.DIAM.set(CRMNGVW.getDouble("E6DIAM"));
            if (CRMNGVW.getErrorCode("E6DIAM") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("DIAM");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6DIAM"), 1));
               return false;
            }
         }
      }
      // Deliver note number.
      if (CRMNGVW.hasField("E6SUDO")) {
         if (!CRMNGVW.isProtected("E6SUDO")) {
            pMaintain.SUDO.set().moveLeftPad(CRMNGVW.getString("E6SUDO"));
         }
      }
      // Delivery note date
      if (CRMNGVW.hasField("E6DNDT")) {
         if (!CRMNGVW.isProtected("E6DNDT")) {
            pMaintain.DNDT.set(CRMNGVW.getDate("E6DNDT"));
            if (pMaintain.DNDT.get() == -1) {
               CRMessageDS.setPXFLDI().moveLeftPad("DNDT");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=WDN2001 Deliver note date &1 is invalid
               COMPMQ("WDN2001", CRMNGVW.getString("E6DNDT"));
               return false;
            }
         }
      }
      // Item number
      if (CRMNGVW.hasField("E6ITNO")) {
         if (!CRMNGVW.isProtected("E6ITNO")) {
            pMaintain.ITNO.set().moveLeftPad(CRMNGVW.getString("E6ITNO"));
         }
      }
      // Invoiced catch weight.
      if (CRMNGVW.hasField("E6IVCW")) {
         if (!CRMNGVW.isProtected("E6IVCW")) {
            pMaintain.IVCW.set(CRMNGVW.getDouble("E6IVCW"));
            if (CRMNGVW.getErrorCode("E6IVCW") != 0) {
               CRMessageDS.setPXFLDI().moveLeftPad("IVCW");
               setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
               // MSGID=XNU0000 Numeric error
               COMPMQ("XNU000" + formatToString(CRMNGVW.getErrorCode("E6IVCW"), 1));
               return false;
            }
         }
      }
      // Alias number.
      if (CRMNGVW.hasField("E6POPN")) {
         if (!CRMNGVW.isProtected("E6POPN")) {
            pMaintain.POPN.set().moveLeftPad(CRMNGVW.getString("E6POPN"));
         }
      }
      return true;
   }
   
   /**
   * Handles messages returned from the function program.
   *
   * @param lineNo
   *    Line number in the subfile
   * @param messages
   *    A reference to the messages.
   * @return
   *    Returns true if a message occured. 
   */
   public boolean handleMessagesInListB(int lineNo, cCRMessageList messages) {
      // handle messages
      // (Don't issue notifications in an open subfile).
      if (messages.exist()) {
         // Check for errors or notifications
         messages.prepareGetNext();
         if (messages.getNextError(CRMessageDS)) {
            // Issue message
            setFocus_on_subfile_B(lineNo, CRMessageDS.getPXFLDI());
            CRCommon.COMPMQ(CRMessageDS);
            return true;
         }
      }
      return false;
   }
   
   /**
   * Sets focus on a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   */
   public void setFocus_on_subfile_B(int lineNo) { 	
      // Set focus on the line
      DSP.setSubfileFocus("BS", "*ROW", lineNo);
   }
   
   /**
   * Sets focus on a field in a line in the subfile on panel B.
   *
   * @param lineNo
   *    Line number
   * @param FLDI
   *    Field ID
   */
   public void setFocus_on_subfile_B(int lineNo, MvxString FLDI) { 	
      // Set focus on the field
      String fieldName = "E6" + FLDI.toStringRTrim();
      if (CRMNGVW.hasField(fieldName) && !CRMNGVW.isProtected(fieldName)) {
         DSP.setSubfileFocus("BS", fieldName, lineNo); 	
      } else {
         // Try to set focus on first open field instead
         boolean openColumnFound = false;
         MvxString columnName = null;
         for (int column = 0; column < 30; column++) {
            columnName = CRMNGVW.getField(column);
            if (columnName.isBlank()) {
               break;
            }
            if (!CRMNGVW.isProtected(columnName)) {
               openColumnFound = true;
               break;
            }
         }
         if (openColumnFound) {
            DSP.setSubfileFocus("BS", columnName.toStringRTrim(), lineNo); 	
         } else {
            DSP.setSubfileFocus("BS", "*ROW", lineNo);
         }
      }
   } 	

   /**
   * C-panel - initiate.
   */
   public void PCINZ() {
      preparePanelC = true;
      // Call APS451Fnc, copy - step initiate
      // =========================================
      pCopy = get_pCopy();
      pCopy.messages.forgetNotifications();
      pCopy.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBL) {
         // Set primary keys
         // - Invoice batch number
         pCopy.INBN.set(APIBL.getINBN());
         // - Division
         pCopy.DIVI.set().moveLeftPad(APIBL.getDIVI());
         // - Transaction number
         pCopy.TRNO.set(APIBL.getTRNO());
      }
      // Pass a reference to the record.
      pCopy.APIBL = APIBL;
      pCopy.passFAPIBL = passFAPIBL;
      passFAPIBL = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pCopy.messages, 'C', false);
      // Release resources allocated by the parameter list.
      pCopy.release();
   }

   /**
   * C-panel - display - initiate.
   */
   public void PCDSP_INZ() {
      // Set last panel
      lastPanel = 'C';
      // Check if the display fields should be prepared
      if (preparePanelC) {
         preparePanelC = false;
         PCDSP_INZ_prepare();
      }
   }
   
   /**
   * C-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PCDSP_INZ_prepare() {
      // Division
      DSP.WCDIVI.moveLeftPad(pCopy.DIVI.get());
      IN01 = pMaintain.DIVI.isAccessOUT();
      IN21 = pMaintain.DIVI.isAccessDISABLED();
      // Copy to Division
      DSP.CPDIVI.moveLeftPad(pCopy.CPDIVI.get());
      IN04 = pMaintain.DIVI.isAccessOUT();
      IN24 = pMaintain.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WCINBN = pCopy.INBN.get();
      IN02 = pMaintain.DIVI.isAccessOUT();
      IN22 = pMaintain.DIVI.isAccessDISABLED();
      // Copy to Invoice batch number
      DSP.CPINBN = pCopy.CPINBN.get();
      IN05 = pMaintain.DIVI.isAccessOUT();
      IN25 = pMaintain.DIVI.isAccessDISABLED();
      // Transaction number
      DSP.WCTRNO = pCopy.TRNO.get();
      IN03 = pMaintain.DIVI.isAccessOUT();
      IN23 = pMaintain.DIVI.isAccessDISABLED();
      // Copy to Transaction number will be set automatically at write.
      // Transaction number
      DSP.CPTRNO = pCopy.CPTRNO.get();
      IN06 = pMaintain.DIVI.isAccessOUT();
      IN26 = pMaintain.DIVI.isAccessDISABLED();
   }

   /**
   * C-panel - check.
   */
   public void PCCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS451Fnc, delete - step validate
      // =========================================
      pCopy.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PCCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelC = true;
      // =========================================
      // Handle messages
      handleMessages(pCopy.messages, 'C', false);
      if (pCopy.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pCopy.release();
   }
   
   /**
   * C-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PCCHK_prepare() {
      // Copy to Division
      if (pCopy.CPDIVI.isAccessMANDATORYorOPTIONAL()) {
         pCopy.CPDIVI.set().moveLeftPad(DSP.CPDIVI);
      }
      // Copy to Invoice batch number
      if (pCopy.CPINBN.isAccessMANDATORYorOPTIONAL()) {
         pCopy.CPINBN.set(DSP.CPINBN);
      }
      // Copy to Transaction number
      // Transaction number will be set automatically at write
      return true;
   }

   /**
   * C-panel - update.
   */
   public void PCUPD() {
      // Declaration
      boolean error = false;
      // Call APS451Fnc, maintain - step update
      // =========================================
      pCopy.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pCopy.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pCopy.messages, transStatus);
      preparePanelC = true;
      // =========================================
      // Handle messages
      error = handleMessages(pCopy.messages, 'C', false);
      // Release resources allocated by the parameter list.
      pCopy.release();
      // Next step
      if (!error) {
         if (this.PXO.charAt(1) == '1') {
            // Update copied data
            this.SQ = 1;
            picSet(this.SEQ.charAt(this.SQ - 1), 'I');
            CRCommon.setChangeMode();
         } else {
            // Return to start panel
            picPop();
         }
         // Set new key values
         APIBL.setDIVI().moveLeftPad(pCopy.CPDIVI.get());
         APIBL.setINBN(pCopy.CPINBN.get());
         APIBL.setTRNO(pCopy.CPTRNO.get());
      }
   }

   /**
   * C-panel - prompt.
   */
   public boolean PCPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PCPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }
   
   /**
   * C-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PCPMT_perform() {
      // ----------------------------------------------------------------
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_C(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WCDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WCINBN");
      } else if (FLDI.EQ("TRNO")) {  DSP.setFocus("WCTRNO");
      } else if (FLDI.EQ("CPDIVI")) {  DSP.setFocus("CPDIVI");
      } else if (FLDI.EQ("CPINBN")) {  DSP.setFocus("CPINBN");
      } else if (FLDI.EQ("CPTRNO")) {  DSP.setFocus("CPTRNO");
      }
   }

   /**
   * D-panel - initiate.
   */
   public void PDINZ() {
      preparePanelD = true;
      // Call APS451Fnc, delete - step initiate
      // =========================================
      pDelete = get_pDelete();
      pDelete.messages.forgetNotifications();
      pDelete.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBL) {
         // Set primary keys
         // - Invoice batch number
         pDelete.INBN.set(APIBL.getINBN());
         // - Division
         pDelete.DIVI.set().moveLeftPad(APIBL.getDIVI());
         // - Transaction number
         pDelete.TRNO.set(APIBL.getTRNO());
      }
      // Pass a reference to the record.
      pDelete.APIBL = APIBL;
      pDelete.passFAPIBL = passFAPIBL;
      passFAPIBL = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pDelete.messages, 'D', false);
      // Release resources allocated by the parameter list.
      pDelete.release();
   }

   /**
   * D-panel - display - initiate.
   */
   public void PDDSP_INZ() {
      // Set last panel
      lastPanel = 'D';
      // Check if the display fields should be prepared
      if (preparePanelD) {
         preparePanelD = false;
         PDDSP_INZ_prepare();
      }
   }
   
   /**
   * D-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PDDSP_INZ_prepare() {
      // Division
      DSP.WDDIVI.moveLeftPad(pDelete.DIVI.get());
      IN01 = pDelete.DIVI.isAccessOUT();
      IN21 = pDelete.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WDINBN = pDelete.INBN.get();
      // Supplier invoice number
      DSP.WDSINO.moveLeftPad(pDelete.SINO.get());
      // Payee
      DSP.WDSPYN.moveLeftPad(pDelete.SPYN.get());
      // Supplier
      DSP.WDSUNO.moveLeftPad(pDelete.SUNO.get());
      // Line type
      DSP.WDRDTP = pDelete.RDTP.get();
      // Line type
      DSP.WDTRNO = pDelete.TRNO.get();
      // Net amount
      this.PXDCCD = pDelete.NLAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'J';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pDelete.NLAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WDNLAM.moveRight(this.PXALPH);
   }

   /**
   * D-panel - check.
   */
   public void PDCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS451Fnc, delete - step validate
      // =========================================
      pDelete.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PDCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelD = true;
      // =========================================
      // Handle messages
      handleMessages(pDelete.messages, 'D', false);
      if (pDelete.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pDelete.release();
   }
   
   /**
   * D-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PDCHK_prepare() {
      // Nothing to validate
      return true;
   }

   /**
   * D-panel - update.
   */
   public void PDUPD() {
      // Declaration
      boolean error = false;
      // Call APS451Fnc, delete - step update
      // =========================================
      pDelete.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pDelete.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pDelete.messages, transStatus);
      preparePanelD = true;
      // =========================================
      // Handle messages
      error = handleMessages(pDelete.messages, 'D', false);
      // Release resources allocated by the parameter list.
      pDelete.release();
      // Next step
      if (!error) {
         picPop();
      }
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_D(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("TRNO")) {  DSP.setFocus("WDTRNO");
      }
   }

   /**
   * E-panel - initiate.
   */
   public void PEINZ() {
      preparePanelE = true;
      // Call APS451Fnc, maintain - step initiate
      // =========================================
      pMaintain = get_pMaintain();
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.INITIATE);
      // Save option to be able to see if option 22 is used
      // Set key parameters
      if (!passFAPIBL) {
         // Set primary keys
         // - Division
         pMaintain.DIVI.set().moveLeftPad(APIBL.getDIVI());
         // - Invoice batch number
         pMaintain.INBN.set(APIBL.getINBN());
         // - Transaction number
         pMaintain.TRNO.set(APIBL.getTRNO());
         //- Line type
         if (CRCommon.getMode() == cEnumMode.ADD && IN62) {
            pMaintain.RDTP.set(DSP.W2RDTP);
         }
         //- Item number
         if (CRCommon.getMode() == cEnumMode.ADD && IN63) {
            pMaintain.ITNO.set().moveLeftPad(DSP.W3ITNO);
         }
         //- Delivery note number
         if (CRCommon.getMode() == cEnumMode.ADD && IN64) {
            pMaintain.SUDO.set().moveLeftPad(DSP.W4SUDO);
         }
      }
      // Pass a reference to the record.
      pMaintain.APIBL = APIBL;
      pMaintain.passFAPIBL = passFAPIBL;
      passFAPIBL = false;
      passExtensionTable(); // exit point for modification
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Check whether to abort bookmark
      if (errorOnInitiate && CRCommon.abortBookmark()) {
         SETLR();
      }
   }

   /**
   * E-panel - display - initiate.
   */
   public void PEDSP_INZ() {
      // Set last panel
      lastPanel = 'E';
      // Check if text exists
      IN57 = pMaintain.TXID.get() != 0L;
      // Check if the display fields should be prepared
      if (preparePanelE) {
         preparePanelE = false;
         PEDSP_INZ_prepare();
      }
   }
   
   /**
   * E-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PEDSP_INZ_prepare() {
      // Set dynamic caption for GLAM
      COMRTM(/*message ID*/ pMaintain.GLAM.getCaption().getID() + "15", /*message file*/ pMaintain.GLAM.getCaption().getLNFP() + "CON");
      DSP.WCGLAM.moveLeft(SRCOMRCM.MSG);
      // Division
      DSP.WEDIVI.moveLeftPad(pMaintain.DIVI.get());
      IN01 = pMaintain.DIVI.isAccessOUT();
      IN21 = pMaintain.DIVI.isAccessDISABLED();
      // Invoice batch number
      DSP.WEINBN = pMaintain.INBN.get();
      IN19 = pMaintain.INBN.isAccessOUT();
      IN39 = pMaintain.INBN.isAccessDISABLED();
      // Transaction number
      DSP.WETRNO = pMaintain.TRNO.get();
      IN02 = pMaintain.TRNO.isAccessOUT();
      IN22 = pMaintain.TRNO.isAccessDISABLED();
      // Invoice batch type
      DSP.WEIBTP.moveLeftPad(pMaintain.IBTP.get());
      IN03 = pMaintain.IBTP.isAccessOUT();
      IN23 = pMaintain.IBTP.isAccessDISABLED();
      // Supplier invoice number
      DSP.WESINO.moveLeftPad(pMaintain.SINO.get());
      IN19 = pMaintain.SINO.isAccessOUT();
      IN39 = pMaintain.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WESUPA = pMaintain.SUPA.get();
      IN03 = pMaintain.SUPA.isAccessOUT();
      IN23 = pMaintain.SUPA.isAccessDISABLED();
      // Line type
      DSP.WERDTP = pMaintain.RDTP.get();
      IN04 = pMaintain.RDTP.isAccessOUT();
      IN24 = pMaintain.RDTP.isAccessDISABLED();
      // Invoice line error
      DSP.WEIBLE = pMaintain.IBLE.get();
      IN05 = pMaintain.IBLE.isAccessOUT();
      IN25 = pMaintain.IBLE.isAccessDISABLED();
      // Error Message
      DSP.WEMSGD.moveLeftPad(pMaintain.MSGD.get());
      IN25 = pMaintain.MSGD.isAccessDISABLED();
      // Service code
      DSP.WESERS = pMaintain.SERS.get();
      IN06 = pMaintain.SERS.isAccessOUT();
      IN26 = pMaintain.SERS.isAccessDISABLED();
      // Service code description
      DSP.WTSERS.moveLeftPad(pMaintain.SERS.getShortAddInfo());
      // Net amount
      this.PXDCCD = pMaintain.NLAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.NLAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WENLAM.moveRight(this.PXALPH);
      IN08 = pMaintain.NLAM.isAccessOUT();
      IN28 = pMaintain.NLAM.isAccessDISABLED();
      // Adjusted amount
      this.PXDCCD = pMaintain.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEADAB.moveRight(this.PXALPH);
      IN07 = pMaintain.ADAB.isAccessOUT();
      IN27 = pMaintain.ADAB.isAccessDISABLED();
      // Currency
      DSP.WECUCD.moveLeftPad(pMaintain.CUCD.get());
      // Purchase order number
      DSP.WEPUNO.moveLeftPad(pMaintain.PUNO.get());
      IN09 = pMaintain.PUNO.isAccessOUT();
      IN29 = pMaintain.PUNO.isAccessDISABLED();
      // VAT amount 1
      this.PXDCCD = pMaintain.VTA1.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.VTA1.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEVTA1.moveRight(this.PXALPH);
      IN10 = pMaintain.VTA1.isAccessOUT();
      IN30 = pMaintain.VTA1.isAccessDISABLED();
      // PO line.
      DSP.WEPNLI = pMaintain.PNLI.get();
      IN11 = pMaintain.PNLI.isAccessOUT();
      IN31 = pMaintain.PNLI.isAccessDISABLED();
      // PO line sub number.
      DSP.WEPNLS = pMaintain.PNLS.get();
      IN11 = pMaintain.PNLS.isAccessOUT();
      IN31 = pMaintain.PNLS.isAccessDISABLED();
      // VAT amount 2
      this.PXDCCD = pMaintain.VTA2.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.VTA2.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEVTA2.moveRight(this.PXALPH);
      IN10 = pMaintain.VTA2.isAccessOUT();
      IN30 = pMaintain.VTA2.isAccessDISABLED();
      // Invoiced quantity
      this.PXDCCD = pMaintain.IVQA.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.IVQA.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEIVQA.moveRight(this.PXALPH);
      IN67 = pMaintain.IVQA.isAccessOUT();
      IN77 = pMaintain.IVQA.isAccessDISABLED();
      // U/M (Invoiced qty).
      DSP.WEPUUN.moveLeftPad(pMaintain.PUUN.get());
      IN67 = pMaintain.PUUN.isAccessOUT();
      IN77 = pMaintain.PUUN.isAccessDISABLED();
      // VAT code.
      DSP.WEVTCD = pMaintain.VTCD.get();
      IN13 = pMaintain.VTCD.isAccessOUT();
      IN33 = pMaintain.VTCD.isAccessDISABLED();
      // VAT rate 1.
      this.PXNUM = pMaintain.VTP1.get();
      this.PXALPH.clear();
      this.PXDCCD = 2;
      this.PXFLDD = 5;
      this.PXEDTC = 'P'; 	
      this.PXDCFM = LDAZD.DCFM;
      SRCOMNUM.COMNUM();
      DSP.WEVTP1.moveRight(this.PXALPH);
      // IN82/IN70 is used for both VTP1 and VTP2
      IN70 = pMaintain.VTP1.isAccessOUT();
      IN82 = pMaintain.VTP1.isAccessDISABLED();
      // VAT rate 2.
      this.PXNUM = pMaintain.VTP2.get();
      this.PXALPH.clear();
      this.PXDCCD = 2;
      this.PXFLDD = 5;
      this.PXEDTC = 'P'; 	
      this.PXDCFM = LDAZD.DCFM;
      SRCOMNUM.COMNUM();
      DSP.WEVTP2.moveRight(this.PXALPH);
      // IN82/IN70 is used for both VTP1 and VTP2
      IN70 = pMaintain.VTP2.isAccessOUT();
      IN82 = pMaintain.VTP2.isAccessDISABLED();
      // Gross price
      this.PXNUM = pMaintain.GRPR.get();
      this.PXALPH.clear();
      SRCOMPRI.PXPECD = 1;
      SRCOMPRI.PXPDCC = pMaintain.GRPR.getDecimals();
      this.PXDCFM = LDAZD.DCFM;
      this.PXEDTC = ' ';
      this.PXADJC = ' ';
      SRCOMPRI.COMPRI();
      DSP.WEGRPR.moveRight(this.PXALPH);
      IN66 = pMaintain.GRPR.isAccessOUT();
      IN76 = pMaintain.GRPR.isAccessDISABLED();
      // U/M (Gross price).
      DSP.WEPPUN.moveLeftPad(pMaintain.PPUN.get());
      IN66 = pMaintain.PPUN.isAccessOUT();
      IN76 = pMaintain.PPUN.isAccessDISABLED();
      // Self billing agreement number.
      DSP.WESBAN.moveLeftPad(pMaintain.SBAN.get());
      IN14 = pMaintain.SBAN.isAccessOUT();
      IN34 = pMaintain.SBAN.isAccessDISABLED();
      // Net price
      this.PXNUM = pMaintain.NEPR.get();
      this.PXALPH.clear();
      SRCOMPRI.PXPECD = 1;
      SRCOMPRI.PXPDCC = pMaintain.NEPR.getDecimals();
      this.PXDCFM = LDAZD.DCFM;
      this.PXEDTC = ' ';
      this.PXADJC = ' ';
      SRCOMPRI.COMPRI();
      DSP.WENEPR.moveRight(this.PXALPH);
      IN12 = pMaintain.NEPR.isAccessOUT();
      IN32 = pMaintain.NEPR.isAccessDISABLED();
      // Sequence number.
      DSP.WECDSE = pMaintain.CDSE.get();
      IN15 = pMaintain.CDSE.isAccessOUT();
      IN35 = pMaintain.CDSE.isAccessDISABLED();
      // Purchase order qty.
      DSP.WEPUCD = pMaintain.PUCD.get();
      IN16 = pMaintain.PUCD.isAccessOUT();
      IN36 = pMaintain.PUCD.isAccessDISABLED();
      // Costing element
      DSP.WECEID.moveLeftPad(pMaintain.CEID.get());
      IN15 = pMaintain.CEID.isAccessOUT();
      IN35 = pMaintain.CEID.isAccessDISABLED();
      // Costing element  text
      DSP.WECHGT.moveLeftPad(pMaintain.CHGT.get());
      IN71 = pMaintain.CHGT.isAccessOUT();
      IN81 = pMaintain.CHGT.isAccessDISABLED();
      // Gross amount
      this.PXDCCD = pMaintain.GLAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.GLAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEGLAM.moveRight(this.PXALPH);
      IN17 = pMaintain.GLAM.isAccessOUT();
      IN37 = pMaintain.GLAM.isAccessDISABLED();
      // Receiving number.
      DSP.WEREPN = pMaintain.REPN.get();
      IN20 = pMaintain.REPN.isAccessOUT();
      IN40 = pMaintain.REPN.isAccessDISABLED();
      // Discount
      this.PXDCCD = pMaintain.DIPC.getDecimals();
      this.PXFLDD = 3 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.DIPC.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEDIPC.moveRight(this.PXALPH);
      IN12 = pMaintain.DIPC.isAccessOUT();
      IN32 = pMaintain.DIPC.isAccessDISABLED();
      // Receipt type.
      DSP.WERELP = pMaintain.RELP.get();
      IN20 = pMaintain.RELP.isAccessOUT();
      IN40 = pMaintain.RELP.isAccessDISABLED();
      // Discount amount
      this.PXDCCD = pMaintain.DIAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.DIAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEDIAM.moveRight(this.PXALPH);
      IN12 = pMaintain.SUDO.isAccessOUT();
      IN32 = pMaintain.SUDO.isAccessDISABLED();
      // Delivery note number
      DSP.WESUDO.moveLeftPad(pMaintain.SUDO.get());
      IN12 = pMaintain.SUDO.isAccessOUT();
      IN32 = pMaintain.SUDO.isAccessDISABLED();
      // Invoice date
      DSP.WEDNDT.moveLeft(CRCalendar.convertDate(pMaintain.DIVI.get(), pMaintain.DNDT.get(), LDAZD.DTFM, ' '));
      IN12 = pMaintain.DNDT.isAccessOUT();
      IN32 = pMaintain.DNDT.isAccessDISABLED();
      // Item number
      DSP.WEITNO.moveLeftPad(pMaintain.ITNO.get());
      IN12 = pMaintain.ITNO.isAccessOUT();
      IN32 = pMaintain.ITNO.isAccessDISABLED();
      // Invoiced catch weight.
      this.PXDCCD = pMaintain.IVCW.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pMaintain.IVCW.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WEIVCW.moveRight(this.PXALPH);
      IN18 = pMaintain.IVCW.isAccessOUT();
      IN38 = pMaintain.IVCW.isAccessDISABLED();
      // Alias number
      DSP.WEPOPN.moveLeftPad(pMaintain.POPN.get());
      IN12 = pMaintain.POPN.isAccessOUT();
      IN32 = pMaintain.POPN.isAccessDISABLED();
      // Claim number
      DSP.WECLAN.moveLeftPad(pMaintain.CLAN.get());
      IN09 = pMaintain.CLAN.isAccessOUT();
      IN29 = pMaintain.CLAN.isAccessDISABLED();
      // Claim line.
      DSP.WECLLN = pMaintain.CLLN.get();
      IN11 = pMaintain.CLLN.isAccessOUT();
      IN31 = pMaintain.CLLN.isAccessDISABLED();
      // Entry date
      DSP.WERGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WELMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pMaintain.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WECHID.moveLeftPad(pMaintain.CHID.get());
   }
   
   /**
   * E-panel - display - roll.
   */
   public void PEDSP_X0E78R() {
      roll(DSP.WETRNO, "WETRNO");
   }

   /**
   * E-panel - check.
   */
   public void PECHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS451Fnc, maintain - step validate
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PECHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelE = true;
      // =========================================
      // Handle messages
      handleMessages(pMaintain.messages, 'E', false);
      if (pMaintain.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pMaintain.release();
   }
   
   /**
   * E-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PECHK_prepare() {
      // Invoice batch type
      if (pMaintain.IBTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.IBTP.set().moveLeftPad(DSP.WEIBTP);
      }
      // Supplier accepted
      if (pMaintain.SUPA.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUPA.set(DSP.WESUPA);
      }
      // Supplier invoice number
      if (pMaintain.SINO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SINO.set().moveLeftPad(DSP.WESINO);
      }
      // Line type
      if (pMaintain.RDTP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.RDTP.set(DSP.WERDTP);
      }
      // Invoice batch line errors
      if (pMaintain.IBLE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.IBLE.set(DSP.WEIBLE);
      }
      // Service code
      if (pMaintain.SERS.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SERS.set(DSP.WESERS);
      }
      // Net amount
      if (pMaintain.NLAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.NLAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WENLAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WENLAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.NLAM.set(this.PXNUM);
      }
      // Purchase order number.
      if (pMaintain.PUNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PUNO.set().moveLeftPad(DSP.WEPUNO);
      }
      // VAT amount 1
      if (pMaintain.VTA1.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.VTA1.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEVTA1);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEVTA1");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTA1.set(this.PXNUM);
      }
      // PO line
      if (pMaintain.PNLI.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PNLI.set(DSP.WEPNLI);
      }
      // PO line sub number
      if (pMaintain.PNLS.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PNLS.set(DSP.WEPNLS);
      }
      // VAT amount 2
      if (pMaintain.VTA2.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.VTA2.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEVTA2);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEVTA2");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTA2.set(this.PXNUM);
      }
      // Invoiced qty
      if (pMaintain.IVQA.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.IVQA.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEIVQA);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEIVQA");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.IVQA.set(this.PXNUM);
      }
      // U/M (Invoiced qty).
      if (pMaintain.PUUN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PUUN.set().moveLeftPad(DSP.WEPUUN);
      }
      // VAT code
      if (pMaintain.VTCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.VTCD.set(DSP.WEVTCD);
      }
      // VAT rate 1
      if (pMaintain.VTP1.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.VTP1.getDecimals();
         this.PXFLDD = 3 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEVTP1);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEVTP1");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTP1.set(this.PXNUM);
      }
      // VAT rate 2
      if (pMaintain.VTP2.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.VTP2.getDecimals();
         this.PXFLDD = 3 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEVTP2);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEVTP2");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.VTP2.set(this.PXNUM);
      }
      // Gross price
      if (pMaintain.GRPR.isAccessMANDATORYorOPTIONAL()) {
         this.PXNUM = 0d;
         this.PXALPH.moveRightPad(DSP.WEGRPR);
         SRCOMPRI.PXPECD = 1;
         SRCOMPRI.PXPDCC = pMaintain.GRPR.getDecimals();
         this.PXDCFM = LDAZD.DCFM;
         this.PXEDTC = ' ';
         this.PXADJC = ' ';
         SRCOMPRI.COMPRI();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEGRPR");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.GRPR.set(this.PXNUM);
      }
      // U/M (Gross price).
      if (pMaintain.PPUN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PPUN.set().moveLeftPad(DSP.WEPPUN);
      }
      // Self billing agreement number.
      if (pMaintain.SBAN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SBAN.set().moveLeftPad(DSP.WESBAN);
      }
      // Net price
      if (pMaintain.NEPR.isAccessMANDATORYorOPTIONAL()) {
         this.PXNUM = 0d;
         this.PXALPH.moveRightPad(DSP.WENEPR);
         SRCOMPRI.PXPECD = 1;
         SRCOMPRI.PXPDCC = pMaintain.NEPR.getDecimals();
         this.PXDCFM = LDAZD.DCFM;
         this.PXEDTC = ' ';
         this.PXADJC = ' ';
         SRCOMPRI.COMPRI();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WENEPR");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.NEPR.set(this.PXNUM);
      }
      // Sequence no
      if (pMaintain.CDSE.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CDSE.set(DSP.WECDSE);
      }
      // Purchase order qty.
      if (pMaintain.PUCD.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.PUCD.set(DSP.WEPUCD);
      }
      // Costing element.
      if (pMaintain.CEID.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CEID.set().moveLeftPad(DSP.WECEID);
      }
      // Costing element text.
      if (pMaintain.CHGT.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CHGT.set().moveLeftPad(DSP.WECHGT);
      }
      // Gross amount
      if (pMaintain.GLAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.GLAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEGLAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEGLAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.GLAM.set(this.PXNUM);
      }
      // Receiving number.
      if (pMaintain.REPN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.REPN.set(DSP.WEREPN);
      }
      // Discount
      if (pMaintain.DIPC.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.DIPC.getDecimals();
         this.PXFLDD = 3 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEDIPC);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEDIPC");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.DIPC.set(this.PXNUM);
      }
      // Receipt type.
      if (pMaintain.RELP.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.RELP.set(DSP.WERELP);
      }
      // Discount amount
      if (pMaintain.DIAM.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.DIAM.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEDIAM);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEDIAM");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.DIAM.set(this.PXNUM);
      }
      // Deliver note number.
      if (pMaintain.SUDO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.SUDO.set().moveLeftPad(DSP.WESUDO);
      }
      // Delivery note date
      if (pMaintain.DNDT.isAccessMANDATORYorOPTIONAL()) {
         if (!DSP.WEDNDT.isBlank()) {
            if (!CRCalendar.convertDate(DSP.WEDIVI, DSP.WEDNDT, LDAZD.DTFM)) {
               picSetMethod('D');
               IN60 = true;
               DSP.setFocus("WEDNDT");
               // MSGID=WDN2001 Delivery note date &1 is invalid
               COMPMQ("WDN2001", DSP.WEDNDT);
               return false;
            }
            pMaintain.DNDT.set(CRCalendar.getDate());
         } else {
            pMaintain.DNDT.clearValue();
         }
      }
      // Item number
      if (pMaintain.ITNO.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.ITNO.set().moveLeftPad(DSP.WEITNO);
      }
      // Invoiced catch weight.
      if (pMaintain.IVCW.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pMaintain.IVCW.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WEIVCW);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WEIVCW");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pMaintain.IVCW.set(this.PXNUM);
      }
      // Alias number
      if (pMaintain.POPN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.POPN.set().moveLeftPad(DSP.WEPOPN);
      }
      // Claim number.
      if (pMaintain.CLAN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CLAN.set().moveLeftPad(DSP.WECLAN);
      }
      // Claim line
      if (pMaintain.CLLN.isAccessMANDATORYorOPTIONAL()) {
         pMaintain.CLLN.set(DSP.WECLLN);
      }
      return true;
   }

   /**
   * E-panel - update.
   */
   public void PEUPD() {
      // Declaration
      boolean error = false;
      // Call APS451Fnc, maintain - step update
      // =========================================
      pMaintain.prepare(CRCommon.getMode(), cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pMaintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pMaintain.messages, transStatus);
      preparePanelE = true;
      // =========================================
      // Handle messages
      error = handleMessages(pMaintain.messages, 'E', false);
      // Release resources allocated by the parameter list.
      pMaintain.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         if (CRCommon.getMode() == cEnumMode.ADD) {
            // End of ADD mode
            picSetMethod('I');
            CRCommon.setChangeMode();
         }
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }

   /**
   * E-panel - prompt.
   */
   public boolean PEPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PEPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

  /**
   * E-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PEPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt Purchase order U/M
      if (DSP.hasFocus("WEPUUN")) {
         if (!DSP.WEITNO.isBlank()) {
            if (cRefPUUNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PUUN.isAccessMANDATORYorOPTIONAL(), 
                ITAUN, APIBL.getCONO(), DSP.WEITNO, DSP.WEPUUN)) 
            {
               DSP.WEPUUN.moveLeftPad(this.PXKVA4);
            }
         } else {
            if (cRefUNIText.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PUUN.isAccessMANDATORYorOPTIONAL(), 
                SYTAB, APIBL.getCONO(), DSP.WEPUUN)) 
            {
               DSP.WEPUUN.moveLeftPad(this.PXKVA4);
            }
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Purchase price U/M
      if (DSP.hasFocus("WEPPUN")) {
         if (!DSP.WEITNO.isBlank()) {
            if (cRefPPUNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PPUN.isAccessMANDATORYorOPTIONAL(), 
                ITAUN, APIBL.getCONO(), DSP.WEITNO, DSP.WEPPUN)) 
            {
               DSP.WEPPUN.moveLeftPad(this.PXKVA4);
               DSP.WEPPU2.moveLeftPad(this.PXKVA4);
            }
         } else {
            if (cRefUNIText.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PPUN.isAccessMANDATORYorOPTIONAL(), 
                SYTAB, APIBL.getCONO(), DSP.WEPPUN)) 
            {
               DSP.WEPPUN.moveLeftPad(this.PXKVA4);
               DSP.WEPPU2.moveLeftPad(this.PXKVA4);
            }
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Costing element
      if (DSP.hasFocus("WECEID")) {
         if (cRefCEIDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.CEID.isAccessMANDATORYorOPTIONAL(), 
             PCELE, APIBL.getCONO(), DSP.WECEID)) 
         {
            DSP.WECEID.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Item number
      if (DSP.hasFocus("WEITNO")) {
         if (cRefITNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.ITNO.isAccessMANDATORYorOPTIONAL(), 
             ITMAS, APIBL.getCONO(), DSP.WEITNO)) 
         {
            DSP.WEITNO.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt VAT code
      if (DSP.hasFocus("WEVTCD")) {
         if (cRefVTCDext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.VTCD.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBL.getCONO(), DSP.WEDIVI, DSP.WEVTCD)) 
         {
            DSP.WEVTCD = this.PXKVA4.getIntLeft(cRefVTCD.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Service code
      if (DSP.hasFocus("WESERS")) {
         if (cRefSERSext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.SERS.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBL.getCONO(), DSP.WESERS)) 
         {
            DSP.WESERS = this.PXKVA4.getIntLeft(cRefSERS.length());
            found_CSYTAB_SERS = cRefSERSext.getCSYTAB_SERS(SYTAB, false, APIBL.getCONO(), DSP.WESERS);
            DSP.WTSERS.moveLeftPad(SYTAB.getTX15());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Claim
      if (DSP.hasFocus("WECLAN")) {
         if (cRefCLANext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.CLAN.isAccessMANDATORYorOPTIONAL(), 
             PCLAH, APIBL.getCONO(), DSP.WECLAN)) 
         {
            DSP.WECLAN.moveLeftPad(this.PXKVA2);
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt Claim line
      if (DSP.hasFocus("WECLLN")) {
         if (cRefCLLNext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.CLLN.isAccessMANDATORYorOPTIONAL(), 
             PCLAL, APIBL.getCONO(), DSP.WECLAN, DSP.WECLLN)) 
         {
            DSP.WECLLN = this.PXKVA3.getIntLeft(cRefCLLN.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt purchase order
      if (DSP.hasFocus("WEPUNO")) {
         if (cRefPUNOext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PUNO.isAccessMANDATORYorOPTIONAL(), 
             PHEAD, APIBL.getCONO(), DSP.WEPUNO)) 
         {
            DSP.WEPUNO.moveLeft(this.PXKVA2);
            DSP.WEPNLI = 0;
            DSP.WEPNLS = 0;
         }
         return true;
      }
      // ----------------------------------------------------------------
      // Prompt purchase order line
      if (DSP.hasFocus("WEPNLI") || DSP.hasFocus("WEPNLS")) {
         if (cRefPNLIext.prompt(this, /*maintain*/ true, /*select*/ pMaintain.PNLI.isAccessMANDATORYorOPTIONAL(), 
             PLINE, APIBL.getCONO(), DSP.WEPUNO, DSP.WEPNLI, DSP.WEPNLS)) 
         {
            DSP.WEPNLI = this.PXKVA3.getIntLeft(cRefPNLI.length());
            DSP.WEPNLS = this.PXKVA4.getIntLeft(cRefPNLS.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }
   
   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("TRNO")
          || FLDI.EQ("IBTP")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("SINO")
          || FLDI.EQ("RDTP")
          || FLDI.EQ("IBLE")
          || FLDI.EQ("SERS")
          || FLDI.EQ("NLAM")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("PUNO")
          || FLDI.EQ("VTA1")
          || FLDI.EQ("PNLI")
          || FLDI.EQ("PNLS")
          || FLDI.EQ("VTA2")
          || FLDI.EQ("IVQA")
          || FLDI.EQ("PUUN")
          || FLDI.EQ("VTCD")
          || FLDI.EQ("GRPR")
          || FLDI.EQ("PPUN")
          || FLDI.EQ("SBAN")
          || FLDI.EQ("NEPR")
          || FLDI.EQ("PPU2")
          || FLDI.EQ("CDSE")
          || FLDI.EQ("PUCD")
          || FLDI.EQ("CEID")
          || FLDI.EQ("GLAM")
          || FLDI.EQ("REPN")
          || FLDI.EQ("DIPC")
          || FLDI.EQ("RELP")
          || FLDI.EQ("DIAM")
          || FLDI.EQ("SUDO")
          || FLDI.EQ("DNDT")
          || FLDI.EQ("ITNO")
          || FLDI.EQ("IVCW")
          || FLDI.EQ("POPN")
          || FLDI.EQ("VTP1")
          || FLDI.EQ("VTP2")
          || FLDI.EQ("CLAN")
          || FLDI.EQ("CLLN")
          || FLDI.EQ("CHGT")
          || FLDI.EQ("CHID")
          || FLDI.EQ("LMDT")
          || FLDI.EQ("RGDT")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_E(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WEDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WEINBN");
      } else if (FLDI.EQ("TRNO")) {  DSP.setFocus("WETRNO");
      } else if (FLDI.EQ("IBTP")) {  DSP.setFocus("WEIBTP");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WESUPA");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WESINO");
      } else if (FLDI.EQ("RDTP")) {  DSP.setFocus("WERDTP");
      } else if (FLDI.EQ("IBLE")) {  DSP.setFocus("WEIBLE");
      } else if (FLDI.EQ("SERS")) {  DSP.setFocus("WESERS");
      } else if (FLDI.EQ("NLAM")) {  DSP.setFocus("WENLAM");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WEADAB");
      } else if (FLDI.EQ("PUNO")) {  DSP.setFocus("WEPUNO");
      } else if (FLDI.EQ("VTA1")) {  DSP.setFocus("WEVTA1");
      } else if (FLDI.EQ("PNLI")) {  DSP.setFocus("WEPNLI");
      } else if (FLDI.EQ("PNLS")) {  DSP.setFocus("WEPNLS");
      } else if (FLDI.EQ("VTA2")) {  DSP.setFocus("WEVTA2");
      } else if (FLDI.EQ("IVQA")) {  DSP.setFocus("WEIVQA");
      } else if (FLDI.EQ("PUUN")) {  DSP.setFocus("WEPUUN");
      } else if (FLDI.EQ("VTCD")) {  DSP.setFocus("WEVTCD");
      } else if (FLDI.EQ("GRPR")) {  DSP.setFocus("WEGRPR");
      } else if (FLDI.EQ("PPUN")) {  DSP.setFocus("WEPPUN");
      } else if (FLDI.EQ("SBAN")) {  DSP.setFocus("WESBAN");
      } else if (FLDI.EQ("NEPR")) {  DSP.setFocus("WENEPR");
      } else if (FLDI.EQ("PPU2")) {  DSP.setFocus("WEPPU2");
      } else if (FLDI.EQ("CDSE")) {  DSP.setFocus("WECDSE");
      } else if (FLDI.EQ("PUCD")) {  DSP.setFocus("WEPUCD");
      } else if (FLDI.EQ("CEID")) {  DSP.setFocus("WECEID");
      } else if (FLDI.EQ("GLAM")) {  DSP.setFocus("WEGLAM");
      } else if (FLDI.EQ("REPN")) {  DSP.setFocus("WEREPN");
      } else if (FLDI.EQ("DIPC")) {  DSP.setFocus("WEDIPC");
      } else if (FLDI.EQ("RELP")) {  DSP.setFocus("WERELP");
      } else if (FLDI.EQ("DIAM")) {  DSP.setFocus("WEDIAM");
      } else if (FLDI.EQ("SUDO")) {  DSP.setFocus("WESUDO");
      } else if (FLDI.EQ("DNDT")) {  DSP.setFocus("WEDNDT");
      } else if (FLDI.EQ("ITNO")) {  DSP.setFocus("WEITNO");
      } else if (FLDI.EQ("IVCW")) {  DSP.setFocus("WEIVCW");
      } else if (FLDI.EQ("POPN")) {  DSP.setFocus("WEPOPN");
      } else if (FLDI.EQ("VTP1")) {  DSP.setFocus("WEVTP1");
      } else if (FLDI.EQ("VTP2")) {  DSP.setFocus("WEVTP2");
      } else if (FLDI.EQ("CLAN")) {  DSP.setFocus("WECLAN");
      } else if (FLDI.EQ("CLLN")) {  DSP.setFocus("WECLLN");
      } else if (FLDI.EQ("CHGT")) {  DSP.setFocus("WECHGT");
      } else if (FLDI.EQ("CHID")) {  DSP.setFocus("WECHID");
      } else if (FLDI.EQ("LMDT")) {  DSP.setFocus("WELMDT");
      } else if (FLDI.EQ("RGDT")) {  DSP.setFocus("WERGDT");
      }
   }

   /**
   * M-panel - initiate.
   */
   public void PMINZ() {
      preparePanelM = true;
      // Call APS450Fnc, adjust line - step initiate
      // =========================================
      pAdjustLine = get_pAdjustLine();
      pAdjustLine.messages.forgetNotifications();
      pAdjustLine.prepare(cEnumStep.INITIATE);
      // Set key parameters
      if (!passFAPIBL) {
         // Set primary keys
         // - Invoice batch number
         pAdjustLine.INBN.set(APIBL.getINBN());
         // - Division
         pAdjustLine.DIVI.set().moveLeftPad(APIBL.getDIVI());
         // - Transaction number
         pAdjustLine.TRNO.set(APIBL.getTRNO());
      }
      // Pass a reference to the record.
      pAdjustLine.APIBL = APIBL;
      pAdjustLine.passFAPIBL = passFAPIBL;
      passFAPIBL = false;
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAdjustLine);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pAdjustLine.messages, 'M', false);
      // Release resources allocated by the parameter list.
      pAdjustLine.release();
   }

   /**
   * M-panel - display - initiate.
   */
   public void PMDSP_INZ() {
      // Set last panel
      lastPanel = 'M';
      // Check if the display fields should be prepared
      if (preparePanelM) {
         preparePanelM = false;
         PMDSP_INZ_prepare();
      }
   }
   
   /**
   * M-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PMDSP_INZ_prepare() {
      // Invoice batch number
      DSP.WMINBN = pAdjustLine.INBN.get();
      IN01 = pAdjustLine.INBN.isAccessOUT();
      IN21 = pAdjustLine.INBN.isAccessDISABLED();
      // Division
      DSP.WMDIVI.moveLeftPad(pAdjustLine.DIVI.get());
      IN02 = pAdjustLine.DIVI.isAccessOUT();
      IN22 = pAdjustLine.DIVI.isAccessDISABLED();
      // Transaction number
      DSP.WMTRNO = pAdjustLine.TRNO.get();
      IN01 = pAdjustLine.TRNO.isAccessOUT();
      IN23 = pAdjustLine.TRNO.isAccessDISABLED();
      // Invoice batch type
      DSP.WMIBTP.moveLeftPad(pAdjustLine.IBTP.get());
      IN04 = pAdjustLine.IBTP.isAccessOUT();
      IN24 = pAdjustLine.IBTP.isAccessDISABLED();
      // Supplier invoice number
      DSP.WMSINO.moveLeftPad(pAdjustLine.SINO.get());
      IN05 = pAdjustLine.SINO.isAccessOUT();
      IN25 = pAdjustLine.SINO.isAccessDISABLED();
      // Invoice status
      DSP.WMSUPA = pAdjustLine.SUPA.get();
      IN06 = pAdjustLine.SUPA.isAccessOUT();
      IN26 = pAdjustLine.SUPA.isAccessDISABLED();
      // Line type
      DSP.WMRDTP = pAdjustLine.RDTP.get();
      IN07 = pAdjustLine.RDTP.isAccessOUT();
      IN27 = pAdjustLine.RDTP.isAccessDISABLED();
      // Line error
      DSP.WMIBLE = pAdjustLine.IBLE.get();
      IN08 = pAdjustLine.IBLE.isAccessOUT();
      IN28 = pAdjustLine.IBLE.isAccessDISABLED();
      // Error Message
      DSP.WMMSGD.moveLeftPad(pAdjustLine.MSGD.get());
      // Net amount
      this.PXDCCD = pAdjustLine.NLAM.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pAdjustLine.NLAM.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WMNLAM.moveRight(this.PXALPH);
      IN11 = pAdjustLine.NLAM.isAccessOUT();
      IN31 = pAdjustLine.NLAM.isAccessDISABLED();
      // Adjusted amount
      this.PXDCCD = pAdjustLine.ADAB.getDecimals();
      this.PXFLDD = 13 + this.PXDCCD;
      this.PXEDTC = 'M';
      this.PXDCFM = LDAZD.DCFM;
      this.PXNUM = pAdjustLine.ADAB.get();
      this.PXALPH.clear();
      SRCOMNUM.PXDCYN = 0;
      SRCOMNUM.COMNUM();
      DSP.WMADAB.moveRight(this.PXALPH);
      IN09 = pAdjustLine.ADAB.isAccessOUT();
      IN29 = pAdjustLine.ADAB.isAccessDISABLED();
      // Currency
      DSP.WMCUCD.moveLeftPad(pAdjustLine.CUCD.get());
      DSP.WMCUC2.moveLeftPad(pAdjustLine.CUCD.get());
      // VAT code
      DSP.WMVTCD = pAdjustLine.VTCD.get();
      IN10 = pAdjustLine.VTCD.isAccessOUT();
      IN30 = pAdjustLine.VTCD.isAccessDISABLED();
      // Entry date
      DSP.WMRGDT.moveLeft(CRCalendar.convertDate_blankDIVI(pAdjustLine.RGDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change date
      DSP.WMLMDT.moveLeft(CRCalendar.convertDate_blankDIVI(pAdjustLine.LMDT.get(), LDAZD.DTFM, LDAZD.DSEP));
      // Change ID
      DSP.WMCHID.moveLeftPad(pAdjustLine.CHID.get());
   }

   /**
   * M-panel - check.
   */
   public void PMCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS450Fnc, Approve - step validate
      // =========================================
      pAdjustLine.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PMCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAdjustLine);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelM = true;
      // =========================================
      // Handle messages
      handleMessages(pAdjustLine.messages, 'M', false);
      if (pAdjustLine.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pAdjustLine.release();
   }
   
   /**
   * M-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PMCHK_prepare() {
      // Adjusted amount
      if (pAdjustLine.ADAB.isAccessMANDATORYorOPTIONAL()) {
         this.PXDCCD = pAdjustLine.ADAB.getDecimals();
         this.PXFLDD = 13 + this.PXDCCD;
         this.PXEDTC = 'M';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = 0d;
         this.PXALPH.clear();
         this.PXALPH.moveRight(DSP.WMADAB);
         SRCOMNUM.PXDCYN = 0;
         SRCOMNUM.COMNUM();
         if (SRCOMNUM.PXNMER != 0) {
            picSetMethod('D');
            IN60 = true;
            DSP.setFocus("WMADAB");
            // MSGID=XNU0000 Numeric error
            COMPMQ("XNU000" + formatToString(SRCOMNUM.PXNMER, 1));
            return false;
         }
         pAdjustLine.ADAB.set(this.PXNUM);
      }
      // VAT code
      if (pAdjustLine.VTCD.isAccessMANDATORYorOPTIONAL()) {
         pAdjustLine.VTCD.set(DSP.WMVTCD);
      }
      return true;
   }

   /**
   * M-panel - update.
   */
   public void PMUPD() {
      // Declaration
      boolean error = false;
      // Call APS451Fnc, adjust line - step update
      // =========================================
      pAdjustLine.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAdjustLine.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAdjustLine.messages, transStatus);
      preparePanelM = true;
      // =========================================
      // Handle messages
      error = handleMessages(pAdjustLine.messages, 'M', false);
      // Release resources allocated by the parameter list.
      pAdjustLine.release();
      // Next step
      if (!error) {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
      }
   }
   
   /**
   * M-panel - prompt.
   */
   public boolean PMPMT() {
      // Next step
      picSetMethod('D');
      DSP.restoreFocus();
      if (!PMPMT_perform()) {
         // MSGID=XF04001 F4 is not permitted in this position on the panel
         COMPMQ("XF04001");
      }
      return true;
   }

   /**
   * M-panel - perform prompting
   * @return
   *    True if prompting was performed.
   */
   public boolean PMPMT_perform() {
      // ----------------------------------------------------------------
      // Prompt VAT code
      if (DSP.hasFocus("WMVTCD")) {
         if (cRefVTCDext.prompt(this, /*maintain*/ true, /*select*/ pAdjustLine.VTCD.isAccessMANDATORYorOPTIONAL(), 
             SYTAB, APIBL.getCONO(), DSP.WMDIVI, DSP.WMVTCD)) 
         {
            DSP.WMVTCD = this.PXKVA4.getIntLeft(cRefVTCD.length());
         }
         return true;
      }
      // ----------------------------------------------------------------
      return false;
   }
   
   /**
   * Checks if the field ID is blank or if it is located on the panel.
   *
   * @param FLDI
   *    Field ID
   * @return
   *    Returns true if the field is blank or is on the panel.
   */
   public boolean isFieldOnPanel_M(MvxString FLDI) {
      if (FLDI.isBlank()
          || FLDI.EQ("DIVI")
          || FLDI.EQ("INBN")
          || FLDI.EQ("TRNO")
          || FLDI.EQ("IBTP")
          || FLDI.EQ("SINO")
          || FLDI.EQ("SUPA")
          || FLDI.EQ("RDTP")
          || FLDI.EQ("IBLE")
          || FLDI.EQ("MSGD")
          || FLDI.EQ("NLAM")
          || FLDI.EQ("ADAB")
          || FLDI.EQ("CUCD")
          || FLDI.EQ("VTCD")
         )
      {
         return true;
      }
      return false;
   }

   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_M(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("DIVI")) {  DSP.setFocus("WMDIVI");
      } else if (FLDI.EQ("INBN")) {  DSP.setFocus("WMINBN");
      } else if (FLDI.EQ("TRNO")) {  DSP.setFocus("WMTRNO");
      } else if (FLDI.EQ("IBTP")) {  DSP.setFocus("WMIBTP");
      } else if (FLDI.EQ("SINO")) {  DSP.setFocus("WMSINO");
      } else if (FLDI.EQ("SUPA")) {  DSP.setFocus("WMSUPA");
      } else if (FLDI.EQ("RDTP")) {  DSP.setFocus("WMRDTP");
      } else if (FLDI.EQ("IBLE")) {  DSP.setFocus("WMIBLE");
      } else if (FLDI.EQ("MSGD")) {  DSP.setFocus("WMMSGD");
      } else if (FLDI.EQ("NLAM")) {  DSP.setFocus("WMNLAM");
      } else if (FLDI.EQ("ADAB")) {  DSP.setFocus("WMADAB");
      } else if (FLDI.EQ("CUCD")) {  DSP.setFocus("WMCUCD");
      } else if (FLDI.EQ("VTCD")) {  DSP.setFocus("WMVTCD");
      }
   }
 
   /**
   * P-panel - initiate.
   */
   public void PPINZ() {
      preparePanelP = true;
      // Call APS451Fnc, settings - step initiate
      // =========================================
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      // Set key parameters
      // - Responsible
      pSettings.RESP.set().move(LDAZD.RESP);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      errorOnInitiate = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
   }
   
   /**
   * P-panel - display - initiate.
   */
   public void PPDSP_INZ() {
      // Set last panel
      lastPanel = 'P';
      // Check if the display fields should be prepared
      if (preparePanelP) {
         preparePanelP = false;
         PPDSP_INZ_prepare();
      }
   }
   
   /**
   * P-panel - display - initiate - prepare display fields.
   * Move function parameters to display fields.
   */
   public void PPDSP_INZ_prepare() {
      // Opening panel
      DSP.WWSPIC = pSettings.SPIC.get().getChar();
      // Default panel sequence
      DSP.WWDSEQ.move(pSettings.DSEQ.get());
   }

   /**
   * P-panel - handle F14.
   */
   public void PPDSP_F14() { 	
      // Create program Meta data 	
      CRMNGVW.createStandardMetaData(LDAZD.CONO, "CrtPgm"); 	
      XXQTTP = -9; 	 	
      XXPAVR.setMax(); 	
      picSetMethod('I'); 	
      return; 	
   }

   /**
   * P-panel - check.
   */
   public void PPCHK() {
      // If error message in step initiate, then do not proceed any further.
      if (errorOnInitiate) {
         picSetMethod('I');
         return;
      }
      // Call APS451Fnc, settings - step validate
      // =========================================
      pSettings.prepare(cEnumStep.VALIDATE);
      // Move display fields to function parameters
      if (!PPCHK_prepare()) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      preparePanelP = true;
      // =========================================
      // Handle messages
      handleMessages(pSettings.messages, 'P', false);
      if (pSettings.isNewEntryContext()) {
         picSetMethod('D');
         IN60 = true;
      }
      // Release resources allocated by the parameter list.
      pSettings.release();
   }

   /**
   * P-panel - check - prepare function parameters.
   * Move display fields to function parameters.
   * @return 
   *    True if all the parameters could be prepared.
   */
   public boolean PPCHK_prepare() {
      // Opening panel
      if (pSettings.SPIC.isAccessMANDATORYorOPTIONAL()) {
         pSettings.SPIC.set().move(DSP.WWSPIC);
      }
      // Default panel sequence
      if (pSettings.DSEQ.isAccessMANDATORYorOPTIONAL()) {
         pSettings.DSEQ.set().move(DSP.WWDSEQ);
         if (!seqCheckSeq(getAllowedPanels(), DSP.WWDSEQ)) {
            return false;
         }
      }
      return true;
   }
   
   /**
   * P-panel - update.
   */
   public void PPUPD() {
      // Declaration
      boolean error = false;
      // Call APS451Fnc, settings - step update
      // =========================================
      pSettings.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      preparePanelP = true;
      // =========================================
      // Handle messages
      error = handleMessages(pSettings.messages, 'P', false);
      // Release resources allocated by the parameter list.
      pSettings.release();
      // Next step
      if (!error) {
         picSetStack(2, DSP.WWSPIC, 'I');
         DSP.WWPSEQ.move(DSP.WWDSEQ);
         this.CSSQ.moveLeft(this.SEQ);
      }
   }
   
   /**
   * Sets focus on a field on the panel.
   *
   * @param FLDI
   *    Field ID
   */
   public void setFocusOnPanel_P(MvxString FLDI) {
      if (FLDI.isBlank()) {
         // No field to set focus on.
      } else if (FLDI.EQ("SPIC")) {  DSP.setFocus("WWSPIC");
      } else if (FLDI.EQ("DSEQ")) {  DSP.setFocus("WWDSEQ");
      }
   }
   
   /**
   * T - panel.
   * Handle program CRS980 - Work with text
   */
   public void PTMAIN() {
      APIBL.CHAIN("00", APIBL.getKey("00"));
      if (F06 == DSP.X0FKEY) {
         DSP.restoreFocus();
      }
      this.PXCONO = APIBL.getCONO();
      this.PXDIVI.clear();
      this.PXFTXH.moveLeft("FSYTXH00");
      this.PXFTXL.moveLeft("FSYTXL00");
      DSINBN.move(APIBL.getINBN());
      DSTRNO.move(APIBL.getTRNO());
      this.PXKFLD.moveLeftPad(DSKFLD);
      this.PXFILE.moveLeft("FAPIBL00");
      this.PXTXID = APIBL.getTXID();
      this.PXLNCD.clear();
      this.PXTXVR.clear();
      this.PXPICC.moveLeft("EI");
      if (CRCommon.getMode() == cEnumMode.RETRIEVE) {
         this.PXOPT2.move(" 5");
      } else {
         this.PXOPT2.move(" 2");
      }
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      // CALL=CRS980 Text. Open
      PXCRS98X.CRS980();
      LDAZZ.FPNM.move(XFPGNM);
      if (APIBL.getTXID() != this.PXTXID) {
         if (APIBL.CHAIN_LOCK("00", APIBL.getKey("00"))) {
            APIBL.setTXID(this.PXTXID);
            APIBL.UPDAT("00");
         }
      }
      if (F06 == DSP.X0FKEY) {
         picPop();
      } else {
         // Next step
         if (F12 == LDAZZ.FKEY) {
            seqPrev();
            picPop();
         } else {
            picSetMethod('D');
            picPush(seqSwitchToNextPanel(), 'I');
         }
      }
   }
   
   /**
   * Roll detail panel.
   * Switches to new record on the detail panels.
   * @param TRNO
   *    The current value.
   * @param fieldName
   *    The name of the display field to set focus on in error message.
   */
   public void roll(int TRNO, String fieldName) {
      // Roll forward
      if (FROLLU == DSP.X0FKEY || F08 == DSP.X0FKEY) {
         APIBL.SETGT("00", APIBL.getKey("00"));
         if (APIBL.READE("00", APIBL.getKey("00", 3))) {
            picSetMethod('I');
            return;
         } else {
            APIBL.SETLL("00", APIBL.getKey("00"));
            if (APIBL.READE("00", APIBL.getKey("00", 3))) {
               picSetMethod('I');
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
               return;
            }
         }
      }
      // Roll backward
      if (FROLLD == DSP.X0FKEY || F07 == DSP.X0FKEY) {
         APIBL.SETLL("00", APIBL.getKey("00"));
         if (APIBL.REDPE("00", APIBL.getKey("00", 3))) {
            picSetMethod('I');
            return;
         } else {
            APIBL.SETLL("00", APIBL.getKey("00"));
            if (APIBL.READE("00", APIBL.getKey("00", 3))) {
               picSetMethod('I');
               // MSGID=XRO0001 You have scrolled past first or last record in file
               COMPMQ("XRO0001");
               return;
            }
         }
      }
      // Check if keyfield is changed
      if (APIBL.getTRNO() != TRNO) {
         // Save key values
         SSTRNO = APIBL.getTRNO();
         APIBL.setTRNO(TRNO);
         if (APIBL.CHAIN("00", APIBL.getKey("00"))) {
            picSetMethod('I');
            return;
         }
         // Restore key values
         APIBL.setTRNO(SSTRNO);
         picSetMethod('D');
         IN60 = true;
         DSP.setFocus(fieldName);
         // MSGID=WTR3103 Transaction number &1 does not exist
         COMPMQ("WTR3103", formatToString(TRNO, cRefTRNO.length()));
         return;
      } else {
         picSetMethod('I');
         picPush(seqSwitchToNextPanel(), 'I');
         return;
      }
   }
   
   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   * @param panel
   *    The current panel.
   * @param suppressNotification
   *    Indicates if notification messages should be suppressed.
   * @return
   *    Returns true if an error occured. If errors are received during step
   * initiate, then it is not allowed to proceed with steps validate and update.
   */
   public boolean handleMessages(cCRMessageList messages, char panel, boolean suppressNotification) {
      // Declaration
      boolean error = false;
      boolean notification = false;
      boolean clearOption = false;
      // handle messages
      if (messages.exist()) {
         // Check for errors relating to the given panel.
         messages.prepareGetNext();
         while (messages.getNextError(CRMessageDS)) {
            if (panel != 'E' && panel != 'M'
                || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
                || panel == 'M' && isFieldOnPanel_M(CRMessageDS.getPXFLDI())
               )
            {
               error = true;
               break;
            }
         }
         // Check for notifications relating to the given panel.
         if (!error) {
            messages.prepareGetNext();
            while (messages.getNextNotification(CRMessageDS)) {
               if (panel != 'E' && panel != 'M'
                   || panel == 'E' && isFieldOnPanel_E(CRMessageDS.getPXFLDI())
                   || panel == 'M' && isFieldOnPanel_M(CRMessageDS.getPXFLDI())
                  )
               {
                  messages.rememberNotification(CRMessageDS.getPXMSID().toStringRTrim());
                  notification = true;
                  break;
               }
            }
         }
         // Issue message
         if (error || notification) {
            if (error && lastPanel =='B') {
               // Display message in B-panel
               switch (panel) {
                  case 'B':
                     clearOption = true;
                     break;
                  case 'C':
                     clearOption = true;
                     PCDSP_F12();
                     break;
                  case 'D':
                     clearOption = true;
                     PDDSP_F12();
                     break;
                  case 'E':
                     clearOption = true;
                     PEDSP_F12();
                     break;
                  case 'M':
                     clearOption = true;
                     PMDSP_F12();
                     break;
                  case 'P':
                     PPDSP_F12();
                     break;
               }
            } else {
               // Display message in detail panel
               switch (panel) {
                  case 'C':
                     setFocusOnPanel_C(CRMessageDS.getPXFLDI());
                     break;
                  case 'D':
                     setFocusOnPanel_D(CRMessageDS.getPXFLDI());
                     break;
                  case 'E':
                     setFocusOnPanel_E(CRMessageDS.getPXFLDI());
                     break;
                  case 'M':
                     setFocusOnPanel_M(CRMessageDS.getPXFLDI());
                     break;
                  case 'P':
                     setFocusOnPanel_P(CRMessageDS.getPXFLDI());
                     break;
               }
            }
            if (error || notification && !suppressNotification) {
               CRCommon.COMPMQ(CRMessageDS);
            }
         } else if (messages.overflow()){
            // Didn't find any message relating to the given panel due to message overflow
            error = true;
            // MSGID=X_00035 Couldn't validate the panel due to many messages on other panels
            COMPMQ("X_00035");
         }
         if (error || notification && !suppressNotification) {
            if (clearOption && DSP.WBRRNA != 0) {
               moveToIN(/*dstOffset*/ 1, DSP.S0INDI); // Restore protect indicators
               DSP.clearOption();
               DSP.XBRRNA = DSP.WBRRNA;
               DSP.updateSFL("BS");
               setFocus_on_subfile_B(DSP.WBRRNA);
            }
            picSetMethod('D');
            IN60 = true;
         }
      }
      return error;
   }

   /**
   * Refresh request sent by UI, called before the actual panel update request is sent
   * @param reason
   */
   public void refresh(String reason) { 	 	 	 	
      refreshReason.moveLeftPad(reason); 	 	 	 	
   } 	 	 	

   /**
   *    subfHead  - Create subfile heading
   */
   public void subfHead() { 	 	 	
      //  Create headings 	 	 	
      setOverridingHeading(); 	 	 	
      CRMNGVW.heading(); 	 	 	 	
      // Move resulting header to display and communicate the layout to UI 	 	 	 	
      DSP.S0SFH.moveLeft(CRMNGVW.SFL); 	 	 	 	 	
      DSP.WWCOLN.moveLeft(CRMNGVW.COLN); 	 	 	 	
      //  Tell the View manager about positioning fields 	 	 	 	
      setPositioningFields(); 	 	 	
      CRMNGVW.calcSubfLayout(DSP); 	 	 	 	
      CRMNGVW.sendColumnCondition(DSP); 	 	 	 	
      return; 	 	 	
   } 	 	 	

   /**
   *    set Overriding Heading  - set override heading (for &-fields and other...)
   */
   public void setOverridingHeading() { 	 	 	
   } 	 	 	

   /**
   *    set Overriding decimals  - set overriding decimals for  numeric fields
   */
   public void setOverridingDecimals() { 	
      // Purchase price
      CRMNGVW.setOverrideNoOfDecimals("E6NLAM", pMaintain.NLAM.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6VTA1", pMaintain.VTA1.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6VTA2", pMaintain.VTA2.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6IVQA", pMaintain.IVQA.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6GRPR", pMaintain.GRPR.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6NEPR", pMaintain.NEPR.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6GLAM", pMaintain.GLAM.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6DIAM", pMaintain.DIAM.getDecimals());
      CRMNGVW.setOverrideNoOfDecimals("E6IVCW", pMaintain.IVCW.getDecimals());
   } 	 	 	

   /**
   *    set Positioning Fields  - set positioning fields for subfile
   */
   public void setPositioningFields() { 	 	 	
      if (IN61) {
         if (!CRMNGVW.setPositioningField("E6TRNO", "W1TRNO")) {
            DSP.W1TRNO = 0; 	
         } 	
      }
      if (IN62) {
         if (!CRMNGVW.setPositioningField("E6RDTP", "W2RDTP")) {
            DSP.W2RDTP = 0; 	
         }
         if (!CRMNGVW.setPositioningField("E6TRNO", "W2TRNO")) {
            DSP.W2TRNO = 0; 	
         }
      }
      if (IN63) {
         if (!CRMNGVW.setPositioningField("E6ITNO", "W3ITNO")) {
            DSP.W3ITNO.clear(); 	
         }
         if (!CRMNGVW.setPositioningField("E6TRNO", "W3TRNO")) {
            DSP.W3TRNO = 0; 	
         }
      }
      if (IN64) {
         if (!CRMNGVW.setPositioningField("E6SUDO", "W4SUDO")) {
            DSP.W4SUDO.clear(); 	
         }
         if (!CRMNGVW.setPositioningField("E6TRNO", "W4TRNO")) {
            DSP.W4TRNO = 0; 	
         }
      }
   } 	 	 	

   /**
   *    set Virtual Field Values  - set values in virtual fields (&-fields)
   */
   public void setVirtualFieldValues() { 	 	 	
   } 	 	 	

   /** 
   * Ensures that tables that are read via cRef-get methods are actually read in the next method call.
   * This is ensured by setting the found-flags to false.
   */
   public void ensureReadOfTables() {
      found_CSYTAB_SERS = false;
   }
   
   /**
   * Processes the bookmark.
   * Prepares the program to process the bookmark with the option given in the bookmark.
   *
   * @return
   *    false if the program shall terminate after processing the bookmark.
   */
   public boolean processBookmark() { 	
      // Get bookmark 	
      Bookmark bookmark = getBookmark(); 	
      // Process bookmark 	
      if (bookmark.getTableName().EQ("FAPIBL")) { 	
         // Validate key values in the bookmark 	
         if(!bookmark.isValidIntField("CONO") ||
            !bookmark.isValidStringField("DIVI") ||
            !bookmark.isValidLongField("INBN") ||
            !bookmark.isValidIntField("TRNO")) 	
         { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	
         // Unpack key values from the bookmark 	
         bookmark_CONO = bookmark.getIntField("CONO");
         bookmark_DIVI.moveLeftPad(bookmark.getStringField("DIVI"));
         bookmark_INBN = bookmark.getLongField("INBN");
         bookmark_TRNO = bookmark.getIntField("TRNO");
         // Check context of the bookmark 	
         if (bookmark_CONO != LDAZD.CONO) { 	
            // MSGID=X_00026 You must log on to company &1 before using the bookmark 	
            bookmark.setError("X_00026", formatToString(bookmark_CONO, 3)); 	
            return false; 	
         } 	
         // Retrieve record 	
         APIBH.setCONO(bookmark_CONO);
         APIBH.setDIVI().move(bookmark_DIVI);
         APIBH.setINBN(bookmark_INBN); 	
         if (!APIBH.CHAIN("00", APIBH.getKey("00"))) { 	
            APIBH.clearNOKEY("00"); 	
         }
         APIBL.setCONO(bookmark_CONO);
         APIBL.setDIVI().move(bookmark_DIVI);
         APIBL.setINBN(bookmark_INBN);
         APIBL.setTRNO(bookmark_TRNO);
         if (!APIBL.CHAIN("00", APIBL.getKey("00"))) { 	
            APIBL.clearNOKEY("00");
            // Dummy save to make it possible to use the key values even though the record was not found
            saved_FAPIBL.setRecord(APIBL);
            APIBL.setRecord(saved_FAPIBL);
         }
         // Determine inquiry settings 	
         startPanel = bookmark.getStartPanel(picGetPanel()); 	
         startPanelSequence.move(bookmark.getPanelSequence(DSP.WWPSEQ)); 	
         startSortingOrder = bookmark.getInquiryType(DSP.WWQTTP);
         if (!bookmark.getView().isBlank()) {
            startView.moveLeftPad(bookmark.getView());
         } 	
         if (CRMNGVW.setStandardFromSortingOrder(startSortingOrder, SYVIU, SYVIP, SYSOR)) {
            CRMNGVW.setDefaultValues(DSP, "WOPAVR", "WOUPVR", DSP.WOPAVR, DSP.WOUPVR, DSP.WWPSEQ, SYSPV, SYSPP, 'B', startSortingOrder, startView, startPanelSequence);
         }
         startPanelSequence.move(DSP.WWPSEQ);
         DSP.WBDIVI.move(bookmark_DIVI);
         DSP.WCDIVI.move(bookmark_DIVI);
         DSP.WDDIVI.move(bookmark_DIVI);
         DSP.WEDIVI.move(bookmark_DIVI);
         DSP.WBINBN = bookmark_INBN;
         // Process the bookmark
         if (startPanel == 'B' && startSortingOrder == 1) { 	
            // - Set B panel key fields and sorting order
            DSP.W1TRNO = bookmark_TRNO;
            // Init sorting order 	
            DSP.WWQTTP = 1; 	
            XXQTTP = DSP.WWQTTP;
            setIndicatorForSortingOrder(DSP.WWQTTP);
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else if (startPanel == 'B' && startSortingOrder == 2) { 	
            // - Set B panel key fields and sorting order 	
            DSP.W2RDTP = APIBL.getRDTP();
            DSP.W2TRNO = bookmark_TRNO;
            // Init sorting order 	
            DSP.WWQTTP = 2; 	
            XXQTTP = DSP.WWQTTP;
            setIndicatorForSortingOrder(DSP.WWQTTP);
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else if (startPanel == 'B' && startSortingOrder == 3) { 	
            // - Set B panel key fields and sorting order 	
            DSP.W3ITNO.moveLeftPad(APIBL.getITNO());
            DSP.W3TRNO = bookmark_TRNO;
            // Init sorting order 	
            DSP.WWQTTP = 3; 	
            XXQTTP = DSP.WWQTTP;
            setIndicatorForSortingOrder(DSP.WWQTTP);
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else if (startPanel == 'B' && startSortingOrder == 4) { 	
            // - Set B panel key fields and sorting order 	
            DSP.W4SUDO.moveLeftPad(APIBL.getSUDO());
            DSP.W4TRNO = bookmark_TRNO;
            // Init sorting order 	
            DSP.WWQTTP = 4; 	
            XXQTTP = DSP.WWQTTP;
            setIndicatorForSortingOrder(DSP.WWQTTP);
            // Process option 	
            if (!processOptionInBookmark(bookmark, startPanel, startPanelSequence, DSP.WSOPT2)) { 	
               // Option already processed. Stop running application 	
               return false; 	
            }
         } else { 	
            // MSGID=X_00025 Invalid bookmark 	
            bookmark.setError("X_00025", ""); 	
            return false; 	
         } 	   	
      } else { 	
         // MSGID=X_00025 Invalid bookmark 	
         bookmark.setError("X_00025", ""); 	
         return false; 	
      } 	
      return true; 	
   } 	

   /**
   * Called to set key fields when processing bookmark in PBCHK.
   */
   public void setKeyFieldsInPBCHKFromBookmark() {
      APIBL.setDIVI().moveLeftPad(bookmark_DIVI); 	
      APIBL.setINBN(bookmark_INBN);
      APIBL.setTRNO(bookmark_TRNO);
   } 	

   /**
   * Called in PBCHK to validate the selected record before processing it as a bookmark.
   */
   public void validateRecordInPBCHKInCaseOfBookmark() { 	
      // Set subfile hidden key fields from record. 	
      DSP.S0DIVI.moveLeftPad(APIBL.getDIVI());
      DSP.S0INBN = APIBL.getINBN();
      DSP.S0TRNO = APIBL.getTRNO();
      // Nothing to check 	
   } 	

   /**
   * Save start values.
   */
   public void SAVSTR() {
      // Update start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      SAVSTR_setValues();
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      pSettings.prepare(cEnumStep.VALIDATE);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (!pSettings.messages.existError()) {
         pSettings.prepare(cEnumStep.UPDATE);
         int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
         CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
      }
      pSettings.release();
   }
   
   /**
   * Set start values before saving.
   */
   public void SAVSTR_setValues() {
      if (!bookmarkInSession(DSPGM)) { 	
         pSettings.QTTP.set(DSP.WWQTTP);
      } 	
   }
   
   /**
   * Set last record - End of program
   */
   public void SETLR() {
      // Clear data in RAM table CSYSPP 	 	
      SYSPP.setCONO(LDAZD.CONO); 	
      SYSPP.setPGNM().moveLeftPad(this.DSPGM); 	
      SYSPP.DELET("00", SYSPP.getKey("00", 2)); 	
      DSP.clearDropDownList("WOPAVR"); 	 	 	
      DSP.clearDropDownList("WOUPVR"); 	
      DSP.clearDropDownList("WWQTTP"); 	
      if (pMaintain != null) {
         pMaintain.messages.forgetNotifications();
      }
      if (pDelete != null) {
         pDelete.messages.forgetNotifications();
      }
      if (pCopy != null) {
         pCopy.messages.forgetNotifications();
      }
      if (pSettings != null) {
         pSettings.messages.forgetNotifications();
      }
      super.SETLR(true);
   }
   
   /**
   * Sets initial settings.
   * Note that some fields already get a default value by the function program.
   */
   public void setInitialSettings() {
      pSettings.SPIC.set().move(LDAZD.SPI1);
      pSettings.SPIC.set().move('B'); // Override SPI1, there is no A-panel in APS451
   }

   /**
   * Validates the sorting order.
   * @param QTTP
   *    Sorting order
   * @return
   *    True if the specified sorting order is valid.
   */
   public boolean validSortingOrder(int QTTP) {
      return QTTP >= 1 && QTTP <=4;
   }

   /**
   * Sets the indicator corresponding to the given sorting order.
   * @param QTTP
   *    Sorting order
   */
   public void setIndicatorForSortingOrder(int QTTP) {
      moveToIN(61, "000000000");  // Clear IN61-69
      moveToIN(60 + QTTP, true);
   }
   
   /**
   * Initiate program.
   */
   public void INIT() {
      if (saved_FAPIBL == null) {
         saved_FAPIBL = APIBL.getEmptyRecord();
      }
      // Init keys
      APIBH.setCONO(LDAZD.CONO);
      APIBL.setCONO(LDAZD.CONO);
      this.PXCONO = LDAZD.CONO; 	
      this.PXDIVI.clear(); 	
      // Check authority
      this.PXPGNM.move(this.DSPGM);
      this.PXAUPF.moveRight(LDAZD.AUPF);
      PXAUTCHK.CAUTCHK();
      this.PXO.move(this.PXALOP);
      // - Not allowed to run program
      if (this.PXALPG == 0) {
         // MSGID=XAU0002 You are not authorized to run the program &1
         SRCOMRCM.MSGLVL.moveLeft("*PRV");
         COMPMQ("XAU0002", formatToString(this.DSPGM));
         SETLR();
         return;
      }
      // Check start values
      pSettings = get_pSettings();
      pSettings.messages.forgetNotifications();
      pSettings.prepare(cEnumStep.INITIATE);
      pSettings.indicateAutomated();
      pSettings.RESP.set().move(LDAZD.RESP);
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
      if (pSettings.messages.exists("XRE0103")) { // Record does not exist
         pSettings.prepare(cEnumStep.VALIDATE);
         setInitialSettings();
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS451Fnc", pSettings);
         LDAZZ.FPNM.move(XFPGNM);
         if (!pSettings.messages.existError()) {
            pSettings.prepare(cEnumStep.UPDATE);
            int transStatus = executeTransaction(pSettings.getTransactionName(), /*returnOnFailure*/ true);
            CRCommon.setDBTransactionErrorMessage(pSettings.messages, transStatus);
         }
      }
      pSettings.release();
      // - Init panel sequence
      this.CSSQ.move(pSettings.DSEQ.get());
      seqLoad();
      // - Init start picture
      picSetPanel(pSettings.SPIC.get().getChar());
      picSetMethod('I');
      // - Init sorting order
      DSP.WWQTTP = pSettings.QTTP.get();
      if (!validSortingOrder(DSP.WWQTTP)) {
         DSP.WWQTTP = 1;
      }
      XXQTTP = -9;
      setIndicatorForSortingOrder(DSP.WWQTTP);
      //   Create program Meta data (if missing) and Create file CSYSPP (View prompt) in QTEMP 	 	
      CRMNGVW.initProgram(LDAZD.CONO, "ChkBildPgm", SYSPP); 	 	
      // Prepare dynamic drop down fields
      CRMNGVW.setDropDownForSorting(DSP, "WWQTTP", SYVIU); 	 	
      // Check for bookmark
      if (bookmarkInSession(DSPGM)) { 	
         // Process bookmark 	
         if (!processBookmark()) { 	
            // Abort after processing the bookmark 	
            SETLR(); 	
            return; 	
         } 	
      } else {
         SETLR();
         return;
      } 	
   }

   String getApName() {
      return "APS451AP";
   }

   // Movex MDB definitions
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBL APIBL;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.CSYSPV SYSPV; 	 	
   public mvx.db.dta.CSYSPP SYSPP; 	 	
   public mvx.db.dta.CSYVIU SYVIU; 	 	 	
   public mvx.db.dta.CSYVIP SYVIP;
   public mvx.db.dta.CSYSOR SYSOR;
   public mvx.db.dta.MITAUN ITAUN;
   public mvx.db.dta.MPCELE PCELE;
   public mvx.db.dta.MITMAS ITMAS;
   public mvx.db.dta.MPCLAH PCLAH;
   public mvx.db.dta.MPHEAD PHEAD;
   public mvx.db.dta.MPLINE PLINE;
   public mvx.db.dta.MPCLAL PCLAL;
   // Movex MDB definitions end

   public void initMDB() {
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBL = (mvx.db.dta.FAPIBL)getMDB("FAPIBL", APIBL);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      SYSPV = (mvx.db.dta.CSYSPV)getMDB("CSYSPV", SYSPV); 	 	 	
      SYSPP = (mvx.db.dta.CSYSPP)getMDB("CSYSPP", SYSPP); 	 	 	 	
      SYVIU = (mvx.db.dta.CSYVIU)getMDB("CSYVIU", SYVIU); 	 	
      SYVIP = (mvx.db.dta.CSYVIP)getMDB("CSYVIP", SYVIP);
      SYSOR = (mvx.db.dta.CSYSOR)getMDB("CSYSOR", SYSOR);
      ITAUN = (mvx.db.dta.MITAUN)getMDB("MITAUN", ITAUN);
      PCELE = (mvx.db.dta.MPCELE)getMDB("MPCELE", PCELE);
      ITMAS = (mvx.db.dta.MITMAS)getMDB("MITMAS", ITMAS);
      PCLAH = (mvx.db.dta.MPCLAH)getMDB("MPCLAH", PCLAH);
      PHEAD = (mvx.db.dta.MPHEAD)getMDB("MPHEAD", PHEAD);
      PLINE = (mvx.db.dta.MPLINE)getMDB("MPLINE", PLINE);
      PCLAL = (mvx.db.dta.MPCLAL)getMDB("MPCLAL", PCLAL);
   }

   public void initDSP() {
      if (DSP == null) {
         DSP = new APS451DSP(this);
      }
   }

   public cPXAPS451FncINmaintain pMaintain = null;
   public cPXAPS451FncINdelete pDelete = null;
   public cPXAPS451FncINcopy pCopy = null;
   public cPXAPS451FncINsettings pSettings = null;
   public cPXAPS451FncINadjustLine pAdjustLine = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINmaintain get_pMaintain() {
      if (pMaintain == null) {
         cPXAPS451FncINmaintain newPlist = new cPXAPS451FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pMaintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pMaintain.allowUpdateWithErrors();
         return pMaintain;
      }
   }
  
  /**
   * Calling APS451Fnc with pMaintain as a transaction.
   */
   @Transaction(name=cPXAPS451FncINmaintain.LOGICAL_NAME)
   public void transaction_APS451FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pMaintain);
      LDAZZ.FPNM.move(XFPGNM);
   }
  
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINdelete get_pDelete() {
      if (pDelete == null) {
         cPXAPS451FncINdelete newPlist = new cPXAPS451FncINdelete();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pDelete.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pDelete.allowUpdateWithErrors();
         return pDelete;
      }
   }
   
   /**
   * Calling APS451Fnc with pDelete as a transaction.
   */
   @Transaction(name=cPXAPS451FncINdelete.LOGICAL_NAME)
   public void transaction_APS451FncINdelete() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pDelete);
      LDAZZ.FPNM.move(XFPGNM);
   }
  
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINcopy get_pCopy() {
      if (pCopy == null) {
         cPXAPS451FncINcopy newPlist = new cPXAPS451FncINcopy();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pCopy.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pCopy.allowUpdateWithErrors();
         return pCopy;
      }
   }
   
   /**
   * Calling APS451Fnc with pCopy as a transaction.
   */
   @Transaction(name=cPXAPS451FncINcopy.LOGICAL_NAME)
   public void transaction_APS451FncINcopy() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pCopy);
      LDAZZ.FPNM.move(XFPGNM);
   }
  
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINsettings get_pSettings() {
      if (pSettings == null) {
         cPXAPS451FncINsettings newPlist = new cPXAPS451FncINsettings();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pSettings.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pSettings.allowUpdateWithErrors();
         return pSettings;
      }
   }
   
   /**
   * Calling APS451Fnc with pSettings as a transaction.
   */
   @Transaction(name=cPXAPS451FncINsettings.LOGICAL_NAME)
   public void transaction_APS451FncINsettings() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pSettings);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINadjustLine get_pAdjustLine() {
      if (pAdjustLine == null) {
         cPXAPS451FncINadjustLine newPlist = new cPXAPS451FncINadjustLine();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         newPlist.allowUpdateWithErrors(); // This is only set for interactive parameter lists (IN) in interactive programs.
         return newPlist;
      } else {
         pAdjustLine.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         pAdjustLine.allowUpdateWithErrors();
         return pAdjustLine;
      }
   }
   
   /**
   * Calling APS451Fnc with pAdjustLine as a transaction.
   */
   @Transaction(name=cPXAPS451FncINadjustLine.LOGICAL_NAME)
   public void transaction_APS451FncINadjustLine() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAdjustLine);
      LDAZZ.FPNM.move(XFPGNM);
   }
  
   public int bookmark_CONO;
   public MvxString bookmark_DIVI = cRefDIVI.likeDef();
   public long bookmark_INBN;
   public int bookmark_TRNO;
   public int bookmark_RDTP;
   public char startPanel; 	
   public MvxString startPanelSequence = cRefPSEQ.likeDef();
   public int startSortingOrder;
   public MvxString startView = cRefPAVR.likeDef();
   public int XXQTTP;
   public int X6RDTP;
   public int X6IBLE;
   public int X6TRNO;
   public int XLRDTP;
   public int XLTRNO;
   public int SSTRNO;
   public long XLINBN;
   public char lastPanel;
   public boolean errorOnInitiate;
   public boolean found_FAPIBL;
   public boolean found_CSYTAB_SERS;
   public boolean passFAPIBL;
   public boolean preparePanelC;
   public boolean preparePanelD;
   public boolean preparePanelE;
   public boolean preparePanelM;
   public boolean preparePanelP;
   public MvxString XXUPVR = cRefPAVR.likeDef();
   public MvxString XXPAVR = cRefPAVR.likeDef();
   public MvxString refreshReason = new MvxString(8);
   public MvxString XFPGNM = cRefPGNM.likeDef();
   public MvxString X6ITNO = cRefITNO.likeDef();
   public MvxString X6SUDO = cRefSUDO.likeDef();
   public MvxString XLITNO = cRefITNO.likeDef();
   public MvxString XLDIVI = cRefDIVI.likeDef();
   public MvxString XLSUDO = cRefSUDO.likeDef();
   public cPXCRS98X PXCRS98X = new cPXCRS98X(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public cSRCOMPRI SRCOMPRI = new cSRCOMPRI(this);
   public cCRMNGVW CRMNGVW = new cCRMNGVW(this); 	 	 	
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public MvxRecord saved_FAPIBL = null;
   public MvxRecord rSBIVAT = new MvxRecord();
   public sPPSBIVATDS PPSBIVATDS = new sPPSBIVATDS(this);
   public MvxString saveSEQ = new MvxString(this.SEQ.length());
   
   public MvxStruct rDSKFLD = new MvxStruct(cRefINBN.length() + cRefTRNO.length());
   public MvxString DSKFLD = rDSKFLD.newString(0, cRefINBN.length() + cRefTRNO.length());
   public MvxString DSINBN = rDSKFLD.newLong(0, cRefINBN.length());
   public MvxString DSTRNO = rDSKFLD.newInt(cRefINBN.length(), cRefTRNO.length());
   
   public APS451DSP DSP;

   public GenericDSP getDSP() {
      return (GenericDSP)DSP;
   }

   /**
   * Called when the program is recycled from the pool to clear class type fields within the program.
   */
   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(DSP);
      v.addElement(APIBH);
      v.addElement(APIBL);
      v.addElement(SYTAB);
      v.addElement(SYSPV);
      v.addElement(SYSPP);
      v.addElement(SYVIU);
      v.addElement(SYVIP);
      v.addElement(SYSOR);
      v.addElement(ITAUN);
      v.addElement(PCELE);
      v.addElement(ITMAS);
      v.addElement(PCLAH);
      v.addElement(PHEAD);
      v.addElement(PLINE);
      v.addElement(pMaintain);
      v.addElement(pDelete);
      v.addElement(pCopy);
      v.addElement(pSettings);
      v.addElement(pAdjustLine);
      v.addElement(startPanelSequence);
      v.addElement(bookmark_DIVI);
      v.addElement(startView);
      v.addElement(XFPGNM);
      v.addElement(PXCRS98X);
      v.addElement(CRMessageDS);
      v.addElement(SRCOMPRI);
      v.addElement(CRMNGVW); 	 	 	 	
      v.addElement(CRCommon);
      v.addElement(CRCalendar);
      v.addElement(XXUPVR); 	 	 	
      v.addElement(XXPAVR); 	 	
      v.addElement(refreshReason);
      v.addElement(X6ITNO);
      v.addElement(X6SUDO);
      v.addElement(XLITNO);
      v.addElement(XLSUDO);
      v.addElement(XLDIVI);
      v.addElement(rDSKFLD);
      v.addElement(PPSBIVATDS);
      v.addElement(PCLAL);
      v.addElement(saveSEQ);
      return version;
   }

   /**
   * Called when the program is recycled from the pool to clear primitive type fields within the program.
   */
   public void clearInstance() {
      super.clearInstance();
      bookmark_CONO = 0;
      bookmark_INBN = 0L;
      bookmark_TRNO = 0;
      bookmark_RDTP = 0;
      startPanel = ' '; 	
      startSortingOrder = 0;
      X6RDTP = 0;
      X6IBLE = 0;
      X6TRNO = 0;
      XLRDTP = 0;
      XLTRNO = 0;
      SSTRNO = 0;
      XLINBN = 0L;
      saved_FAPIBL = null;
      found_FAPIBL = false;
      found_CSYTAB_SERS = false;
      passFAPIBL = false;
      lastPanel = ' ';
      errorOnInitiate = false;
      XXQTTP = 0;
      preparePanelC = false;
      preparePanelD = false;
      preparePanelE = false;
      preparePanelM = false;
      preparePanelP = false;
   }

   public String getBookmarkTableName() { 	
     return "FAPIBL"; 	
   } 	

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getPanelsRequiredForAdd() {
      return APS451Fnc.getPanelsRequiredForAdd();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getAllowedPanels() {
      return APS451Fnc.getAllowedPanels();
   }

   /**
   * If you override the corresponding method in the function program,
   * then you must also override this method. (As is, with no changes).
   * This is needed to make sure the subclass method is called in the function program.
   */
   public String getDefaultPanels() {
      return APS451Fnc.getDefaultPanels();
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="E424785F79B5417d9A6FC028F141181D";

public final static String _tempFixComment="";

public final static String _build="000000000000367";

public final static String _pgmName="APS451";

   public String getVersion() {
      return _version;
   } //·end of method getVersion

   public String getRelease() {
      return _release;
   } //·end of method getRelease

   public String getSpLevel() {
      return _spLevel;
   } //·end of method getSpLevel

   public String getSpNumber() {
      return _spNumber;
   } //·end of method getSpNumber

   public String getGUID() {
      return _GUID;
   } //·end of method getGUID

   public String getTempFixComment() {
      return _tempFixComment;
   } //·end of method getTempFixComment

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   } //·end of method getVersionInformation

   public String getBuild() {
      return (_version + _release + _build + "      " +  _pgmName + "                                   ").substring(0,34);
   } //·end of method getBuild

   public String [][] getStandardModification() {
      return _standardModifications;
   } //·end of method [][] getStandardModification

  public final static String [][] _standardModifications={};
} 	
